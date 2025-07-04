package com.myfinances.ui.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.domain.usecase.GetAccountUseCase
import com.myfinances.domain.usecase.GetActiveAccountIdUseCase
import com.myfinances.domain.usecase.GetExpenseTransactionsUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.toSimpleListItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val getExpenseTransactionsUseCase: GetExpenseTransactionsUseCase,
    private val getActiveAccountIdUseCase: GetActiveAccountIdUseCase,
    private val getAccountUseCase: GetAccountUseCase,
    private val accountUpdateManager: AccountUpdateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExpensesUiState>(ExpensesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
        viewModelScope.launch {
            accountUpdateManager.accountUpdateFlow.collect {
                loadData()
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = ExpensesUiState.Loading

            when (val accountIdResult = getActiveAccountIdUseCase()) {
                is Result.Success -> loadDataForAccount(accountIdResult.data)
                is Result.Error -> {
                    _uiState.value = ExpensesUiState.Error(
                        accountIdResult.exception.message ?: "Не удалось получить активный счет"
                    )
                }
                is Result.NetworkError -> _uiState.value = ExpensesUiState.NoInternet
            }
        }
    }

    private suspend fun loadDataForAccount(accountId: Int) = coroutineScope {
        val accountDeferred = async { getAccountUseCase() }
        val transactionsDeferred = async { getExpenseTransactionsUseCase(accountId) }

        val accountResult = accountDeferred.await()
        val transactionsResult = transactionsDeferred.await()

        if (accountResult is Result.Success && transactionsResult is Result.Success) {
            val account = accountResult.data
            val (transactions, categories) = transactionsResult.data
            val categoryMap = categories.associateBy { it.id }

            val transactionItems = transactions.map { transaction ->
                transaction.toSimpleListItemModel(
                    category = categoryMap[transaction.categoryId],
                    currencyCode = account.currency
                )
            }
            val totalAmount = transactions.sumOf { it.amount }

            _uiState.value = ExpensesUiState.Success(
                transactionItems = transactionItems,
                totalAmount = totalAmount,
                currency = account.currency
            )
        } else {
            val errorResult =
                if (accountResult !is Result.Success) accountResult else transactionsResult
            handleErrorResult(errorResult)
        }
    }

    private fun handleErrorResult(result: Result<*>) {
        when (result) {
            is Result.Error -> _uiState.value =
                ExpensesUiState.Error(result.exception.message ?: "Неизвестная ошибка")

            is Result.NetworkError -> _uiState.value = ExpensesUiState.NoInternet
            else -> {}
        }
    }
}