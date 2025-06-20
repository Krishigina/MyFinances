package com.myfinances.ui.screens.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.usecase.GetTransactionsUseCase
import com.myfinances.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    // ИЗМЕНЕНО: Храним текущие даты в ViewModel
    private var startDate: Date
    private var endDate: Date

    init {
        // Инициализируем даты по умолчанию (текущий месяц)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        endDate = calendar.time

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        startDate = calendar.time

        loadData()
    }

    // ИЗМЕНЕНО: Новый метод для обработки событий от UI
    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.StartDateSelected -> {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = event.timestampMillis
                // Устанавливаем время на начало дня
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                startDate = calendar.time
                loadData() // Перезагружаем данные с новой датой
            }

            is HistoryEvent.EndDateSelected -> {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = event.timestampMillis
                // Устанавливаем время на конец дня
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                endDate = calendar.time
                loadData() // Перезагружаем данные с новой датой
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading

            val filterType = savedStateHandle.get<TransactionTypeFilter>("transactionType")
                ?: TransactionTypeFilter.ALL

            when (
                val result = getTransactionsUseCase(
                    accountId = 1,
                    // Используем сохраненные даты
                    startDate = startDate,
                    endDate = endDate,
                    filter = filterType
                )
            ) {
                is Result.Success -> {
                    _uiState.value = HistoryUiState.Success(
                        transactions = result.data.first.sortedByDescending { it.date },
                        categories = result.data.second,
                        startDate = startDate,
                        endDate = endDate
                    )
                }
                is Result.Error -> {
                    _uiState.value = HistoryUiState.Error(
                        result.exception.message ?: "Failed to load transactions"
                    )
                }
            }
        }
    }
}