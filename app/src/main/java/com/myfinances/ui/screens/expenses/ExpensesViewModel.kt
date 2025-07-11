package com.myfinances.ui.screens.expenses

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.domain.entity.TransactionData
import com.myfinances.domain.usecase.GetExpenseTransactionsUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.TransactionDomainToUiMapper
import com.myfinances.ui.util.formatCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ExpensesViewModel @Inject constructor(
    private val getExpenseTransactionsUseCase: GetExpenseTransactionsUseCase,
    private val accountUpdateManager: AccountUpdateManager,
    private val mapper: TransactionDomainToUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExpensesUiState>(ExpensesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val snackbarHostState = SnackbarHostState()

    init {
        loadData()
        viewModelScope.launch {
            accountUpdateManager.accountUpdateFlow.collect {
                loadData()
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            if (_uiState.value !is ExpensesUiState.Content) {
                _uiState.value = ExpensesUiState.Loading
            }

            when (val result = getExpenseTransactionsUseCase()) {
                is Result.Success -> processSuccess(result.data)
                is Result.Error -> showError(result.exception.message ?: "Неизвестная ошибка")
                is Result.NetworkError -> showError("Ошибка сети. Проверьте подключение.")
            }
        }
    }

    private fun processSuccess(data: TransactionData) {
        val items = data.transactions.map {
            mapper.toSimpleListItemModel(it, data.categories[it.categoryId], data.account.currency)
        }

        _uiState.value = ExpensesUiState.Content(
            transactionItems = items,
            totalAmountFormatted = formatCurrency(data.totalAmount, data.account.currency)
        )

        if (items.isEmpty()) {
            showInfo("За сегодня еще не было расходов")
        }
    }

    private fun showError(message: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(message = message)
        }
        if (_uiState.value is ExpensesUiState.Loading) {
            _uiState.value = ExpensesUiState.Content(emptyList(), "0,00 ₽")
        }
    }

    private fun showInfo(message: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
}