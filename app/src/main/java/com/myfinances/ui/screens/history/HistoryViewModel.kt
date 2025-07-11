package com.myfinances.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.usecase.GetAccountUseCase
import com.myfinances.domain.usecase.GetActiveAccountIdUseCase
import com.myfinances.domain.usecase.GetTransactionsUseCase
import com.myfinances.domain.util.Result
import com.myfinances.domain.util.withTimeAtEndOfDay
import com.myfinances.domain.util.withTimeAtStartOfDay
import com.myfinances.ui.mappers.toHistoryListItemModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getActiveAccountIdUseCase: GetActiveAccountIdUseCase,
    private val getAccountUseCase: GetAccountUseCase,
    private val accountUpdateManager: AccountUpdateManager
) : ViewModel() {

    private lateinit var transactionType: TransactionTypeFilter

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    // Метод для инициализации ViewModel с параметрами из навигации
    fun initialize(filter: TransactionTypeFilter) {
        if (this::transactionType.isInitialized) return // Предотвращаем повторную инициализацию

        this.transactionType = filter

        val calendar = Calendar.getInstance()
        val endDate = calendar.withTimeAtEndOfDay().time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = calendar.withTimeAtStartOfDay().time
        loadData(startDate, endDate)

        viewModelScope.launch {
            accountUpdateManager.accountUpdateFlow.collect {
                val currentState = _uiState.value
                if (currentState is HistoryUiState.Success) {
                    loadData(currentState.startDate, currentState.endDate)
                }
            }
        }
    }

    fun onEvent(event: HistoryEvent) {
        val currentState = _uiState.value
        if (currentState !is HistoryUiState.Success) return

        when (event) {
            is HistoryEvent.StartDateSelected -> {
                val newStartDate = Date(event.timestampMillis)
                if (newStartDate.before(currentState.endDate)) {
                    loadData(newStartDate, currentState.endDate)
                }
            }
            is HistoryEvent.EndDateSelected -> {
                val newEndDate = Date(event.timestampMillis)
                if (newEndDate.after(currentState.startDate)) {
                    loadData(currentState.startDate, newEndDate)
                }
            }
        }
    }

    private fun loadData(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading

            val accountIdResult = getActiveAccountIdUseCase()
            if (accountIdResult is Result.Success) {
                loadDataForAccount(accountIdResult.data, startDate, endDate)
            } else {
                handleErrorResult(accountIdResult)
            }
        }
    }

    private suspend fun loadDataForAccount(accountId: Int, startDate: Date, endDate: Date) =
        coroutineScope {
            val accountDeferred = async { getAccountUseCase() }
            val transactionsDeferred = async {
                getTransactionsUseCase(accountId, startDate, endDate, transactionType)
            }

            val accountResult = accountDeferred.await()
            val transactionsResult = transactionsDeferred.await()

            if (accountResult is Result.Success && transactionsResult is Result.Success) {
                val account = accountResult.data
                val (transactions, categories) = transactionsResult.data
                val categoryMap = categories.associateBy { it.id }

                val transactionItems = transactions.map {
                    it.toHistoryListItemModel(
                        categoryMap[it.categoryId],
                        account.currency
                    )
                }
                val totalAmount = transactions.sumOf { it.amount }

                _uiState.value = HistoryUiState.Success(
                    transactionItems,
                    totalAmount,
                    startDate,
                    endDate,
                    account.currency
                )
            } else {
                handleErrorResult(if (accountResult !is Result.Success) accountResult else transactionsResult)
            }
        }

    private fun handleErrorResult(result: Result<*>) {
        when (result) {
            is Result.Error -> _uiState.value =
                HistoryUiState.Error(result.exception.message ?: "Неизвестная ошибка")

            is Result.NetworkError -> _uiState.value = HistoryUiState.NoInternet
            else -> {}
        }
    }
}