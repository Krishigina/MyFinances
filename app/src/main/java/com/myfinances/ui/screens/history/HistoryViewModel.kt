package com.myfinances.ui.screens.history

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.domain.entity.TransactionData
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.usecase.GetTransactionsUseCase
import com.myfinances.domain.util.Result
import com.myfinances.domain.util.withTimeAtStartOfDay
import com.myfinances.ui.mappers.TransactionDomainToUiMapper
import com.myfinances.ui.model.HistoryUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val accountUpdateManager: AccountUpdateManager,
    private val mapper: TransactionDomainToUiMapper
) : ViewModel() {

    private lateinit var transactionType: TransactionTypeFilter

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val snackbarHostState = SnackbarHostState()

    fun initialize(filter: TransactionTypeFilter) {
        if (this::transactionType.isInitialized) return
        this.transactionType = filter

        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = calendar.withTimeAtStartOfDay().time
        loadData(startDate, endDate)

        viewModelScope.launch {
            accountUpdateManager.accountUpdateFlow.collect {
                (_uiState.value as? HistoryUiState.Content)?.let {
                    loadData(it.uiModel.startDate, it.uiModel.endDate)
                }
            }
        }
    }

    fun onEvent(event: HistoryEvent) {
        val contentState = (_uiState.value as? HistoryUiState.Content) ?: return

        when (event) {
            is HistoryEvent.StartDateSelected -> {
                val newStartDate = Date(event.timestampMillis)
                if (!newStartDate.after(contentState.uiModel.endDate)) {
                    loadData(newStartDate, contentState.uiModel.endDate)
                }
            }
            is HistoryEvent.EndDateSelected -> {
                val newEndDate = Date(event.timestampMillis)
                if (!newEndDate.before(contentState.uiModel.startDate)) {
                    loadData(contentState.uiModel.startDate, newEndDate)
                }
            }
        }
    }

    private fun loadData(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            if (_uiState.value !is HistoryUiState.Content) {
                _uiState.value = HistoryUiState.Loading
            }

            when (val result = getTransactionsUseCase(startDate, endDate, transactionType)) {
                is Result.Success -> processSuccess(result.data)
                is Result.Error -> showError(result.exception.message ?: "Неизвестная ошибка")
                is Result.NetworkError -> showError("Ошибка сети. Проверьте подключение.")
            }
        }
    }

    private fun processSuccess(data: TransactionData) {
        val items = data.transactions.map {
            mapper.toHistoryUiModel(it, data.categories[it.categoryId], data.account.currency)
        }

        val historyUiModel = HistoryUiModel(
            transactionItems = items,
            totalAmount = data.totalAmount,
            currencyCode = data.account.currency,
            startDate = data.startDate,
            endDate = data.endDate
        )

        _uiState.value = HistoryUiState.Content(historyUiModel, transactionType)

        if (items.isEmpty()) {
            showInfo("Нет транзакций за выбранный период")
        }
    }

    private fun showError(message: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(message)
        }
        if (_uiState.value is HistoryUiState.Loading) {
            val calendar = Calendar.getInstance()
            val endDate = calendar.time
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val startDate = calendar.withTimeAtStartOfDay().time
            _uiState.value = HistoryUiState.Content(
                HistoryUiModel(
                    emptyList(), 0.0, "₽", startDate, endDate
                ),
                transactionType
            )
        }
    }

    private fun showInfo(message: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
        }
    }
}