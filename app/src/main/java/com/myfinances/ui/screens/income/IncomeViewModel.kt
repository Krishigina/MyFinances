package com.myfinances.ui.screens.income

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.domain.entity.TransactionData
import com.myfinances.domain.usecase.GetIncomeTransactionsUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.TransactionDomainToUiMapper
import com.myfinances.ui.util.formatCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class IncomeViewModel @Inject constructor(
    private val getIncomeTransactionsUseCase: GetIncomeTransactionsUseCase,
    private val accountUpdateManager: AccountUpdateManager,
    private val mapper: TransactionDomainToUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow<IncomeUiState>(IncomeUiState.Loading)
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
            if (_uiState.value !is IncomeUiState.Content) {
                _uiState.value = IncomeUiState.Loading
            }

            when (val result = getIncomeTransactionsUseCase()) {
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

        _uiState.value = IncomeUiState.Content(
            transactionItems = items,
            totalAmountFormatted = formatCurrency(data.totalAmount, data.account.currency)
        )

        if (items.isEmpty()) {
            showInfo("За сегодня еще не было доходов")
        }
    }

    private fun showError(message: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(message = message)
        }
        if (_uiState.value is IncomeUiState.Loading) {
            _uiState.value = IncomeUiState.Content(emptyList(), "0,00 ₽")
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