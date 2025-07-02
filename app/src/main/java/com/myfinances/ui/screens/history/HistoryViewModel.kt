package com.myfinances.ui.screens.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.usecase.GetActiveAccountIdUseCase
import com.myfinances.domain.usecase.GetTransactionsUseCase
import com.myfinances.domain.util.Result
import com.myfinances.domain.util.withTimeAtEndOfDay
import com.myfinances.domain.util.withTimeAtStartOfDay
import com.myfinances.ui.mappers.toHistoryListItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel для экрана "История".
 * Отвечает за загрузку истории транзакций за выбранный период, управление состоянием
 * экрана и обработку пользовательских событий (выбор даты).
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getActiveAccountIdUseCase: GetActiveAccountIdUseCase,
    private val connectivityManager: ConnectivityManagerSource
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var startDate: Date
    private var endDate: Date

    init {
        val calendar = Calendar.getInstance()
        endDate = calendar.withTimeAtEndOfDay().time

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        startDate = calendar.withTimeAtStartOfDay().time

        observeNetworkStatus()
        loadData()
    }

    private fun observeNetworkStatus() {
        connectivityManager.isNetworkAvailable
            .onEach { isAvailable ->
                if (isAvailable && _uiState.value is HistoryUiState.NoInternet) {
                    loadData()
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.StartDateSelected -> {
                val calendar = Calendar.getInstance().apply { timeInMillis = event.timestampMillis }
                startDate = calendar.withTimeAtStartOfDay().time
                loadData()
            }
            is HistoryEvent.EndDateSelected -> {
                val calendar = Calendar.getInstance().apply { timeInMillis = event.timestampMillis }
                endDate = calendar.withTimeAtEndOfDay().time
                loadData()
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading

            when (val accountIdResult = getActiveAccountIdUseCase()) {
                is Result.Success -> {
                    loadTransactionsForAccount(accountIdResult.data)
                }

                is Result.Error -> {
                    _uiState.value = HistoryUiState.Error(
                        accountIdResult.exception.message ?: "Не удалось получить активный счет"
                    )
                }

                is Result.NetworkError -> {
                    _uiState.value = HistoryUiState.NoInternet
                }
            }
        }
    }

    private suspend fun loadTransactionsForAccount(accountId: Int) {
        val filterType = savedStateHandle.get<TransactionTypeFilter>("transactionType")
            ?: TransactionTypeFilter.ALL

        when (val result = getTransactionsUseCase(
            accountId = accountId,
            startDate = startDate,
            endDate = endDate,
            filter = filterType
        )) {
            is Result.Success -> {
                val transactions = result.data.first.sortedByDescending { it.date }
                val categories = result.data.second
                val categoryMap = categories.associateBy { it.id }

                val transactionItems = transactions.map { transaction ->
                    transaction.toHistoryListItemModel(categoryMap[transaction.categoryId])
                }
                val totalAmount = transactions.sumOf { it.amount }

                _uiState.value = HistoryUiState.Success(
                    transactionItems = transactionItems,
                    totalAmount = totalAmount,
                    startDate = startDate,
                    endDate = endDate
                )
            }

            is Result.Error -> {
                _uiState.value = HistoryUiState.Error(
                    result.exception.message ?: "Не удалось загрузить транзакции"
                )
            }

            is Result.NetworkError -> {
                _uiState.value = HistoryUiState.NoInternet
            }
        }
    }
}