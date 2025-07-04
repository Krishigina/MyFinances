package com.myfinances.ui.screens.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.usecase.GetAccountUseCase
import com.myfinances.domain.usecase.GetActiveAccountIdUseCase
import com.myfinances.domain.usecase.GetTransactionsUseCase
import com.myfinances.domain.util.Result
import com.myfinances.domain.util.withTimeAtEndOfDay
import com.myfinances.domain.util.withTimeAtStartOfDay
import com.myfinances.ui.mappers.toHistoryListItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel для экрана "История".
 *
 * Отвечает за:
 * - Загрузку истории транзакций за выбранный период.
 * - Получение информации о счете для отображения валюты.
 * - Управление состоянием экрана и обработку пользовательских событий (выбор даты).
 * - Получение типа фильтрации (`transactionType`) из `SavedStateHandle`,
 *   который передается при навигации.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getActiveAccountIdUseCase: GetActiveAccountIdUseCase,
    private val getAccountUseCase: GetAccountUseCase
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
        loadData()
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
                is Result.Success -> loadTransactionsForAccount(accountIdResult.data)
                is Result.Error -> _uiState.value = HistoryUiState.Error(
                    accountIdResult.exception.message ?: "Не удалось получить активный счет"
                )

                is Result.NetworkError -> _uiState.value = HistoryUiState.NoInternet
            }
        }
    }

    private suspend fun loadTransactionsForAccount(accountId: Int) = coroutineScope {
        val filterType = savedStateHandle.get<TransactionTypeFilter>("transactionType")
            ?: TransactionTypeFilter.ALL

        val accountDeferred = async { getAccountUseCase() }
        val transactionsDeferred = async {
            getTransactionsUseCase(
                accountId = accountId,
                startDate = startDate,
                endDate = endDate,
                filter = filterType
            )
        }

        val accountResult = accountDeferred.await()
        val transactionsResult = transactionsDeferred.await()

        if (accountResult is Result.Success && transactionsResult is Result.Success) {
            val account = accountResult.data
            val transactions = transactionsResult.data.first.sortedByDescending { it.date }
            val categories = transactionsResult.data.second
            val categoryMap = categories.associateBy { it.id }

            val transactionItems = transactions.map { transaction ->
                transaction.toHistoryListItemModel(
                    category = categoryMap[transaction.categoryId],
                    currencyCode = account.currency
                )
            }
            val totalAmount = transactions.sumOf { it.amount }

            _uiState.value = HistoryUiState.Success(
                transactionItems = transactionItems,
                totalAmount = totalAmount,
                startDate = startDate,
                endDate = endDate,
                currency = account.currency
            )
        } else {
            val errorResult =
                if (accountResult !is Result.Success) accountResult else transactionsResult
            when (errorResult) {
                is Result.Error -> _uiState.value = HistoryUiState.Error(
                    errorResult.exception.message ?: "Не удалось загрузить транзакции"
                )

                is Result.NetworkError -> _uiState.value = HistoryUiState.NoInternet
                else -> {}
            }
        }
    }
}