package com.myfinances.ui.screens.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.usecase.GetAccountUseCase
import com.myfinances.domain.usecase.GetActiveAccountIdUseCase
import com.myfinances.domain.usecase.GetIncomeTransactionsUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.toSimpleListItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана "Доходы".
 *
 * Логика аналогична ExpensesViewModel, но использует [GetIncomeTransactionsUseCase]
 * для загрузки транзакций доходов.
 */
@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val getIncomeTransactionsUseCase: GetIncomeTransactionsUseCase,
    private val getActiveAccountIdUseCase: GetActiveAccountIdUseCase,
    private val getAccountUseCase: GetAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<IncomeUiState>(IncomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = IncomeUiState.Loading

            when (val accountIdResult = getActiveAccountIdUseCase()) {
                is Result.Success -> loadDataForAccount(accountIdResult.data)
                is Result.Error -> {
                    _uiState.value = IncomeUiState.Error(
                        accountIdResult.exception.message ?: "Не удалось получить активный счет"
                    )
                }

                is Result.NetworkError -> _uiState.value = IncomeUiState.NoInternet
            }
        }
    }

    private suspend fun loadDataForAccount(accountId: Int) = coroutineScope {
        val accountDeferred = async { getAccountUseCase() }
        val transactionsDeferred = async { getIncomeTransactionsUseCase(accountId) }

        val accountResult = accountDeferred.await()
        val transactionsResult = transactionsDeferred.await()

        if (accountResult is Result.Success && transactionsResult is Result.Success) {
            val account = accountResult.data
            val transactions = transactionsResult.data.first
            val categories = transactionsResult.data.second
            val categoryMap = categories.associateBy { it.id }

            val transactionItems = transactions.map { transaction ->
                transaction.toSimpleListItemModel(
                    category = categoryMap[transaction.categoryId],
                    currencyCode = account.currency
                )
            }
            val totalAmount = transactions.sumOf { it.amount }

            _uiState.value = IncomeUiState.Success(
                transactionItems = transactionItems,
                totalAmount = totalAmount,
                currency = account.currency
            )
        } else {
            val errorResult =
                if (accountResult !is Result.Success) accountResult else transactionsResult
            when (errorResult) {
                is Result.Error -> _uiState.value =
                    IncomeUiState.Error(errorResult.exception.message ?: "Неизвестная ошибка")

                is Result.NetworkError -> _uiState.value = IncomeUiState.NoInternet
                else -> {}
            }
        }
    }
}