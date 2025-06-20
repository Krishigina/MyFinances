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
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading

            val filterType = savedStateHandle.get<TransactionTypeFilter>("transactionType")
                ?: TransactionTypeFilter.ALL

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            val endDate = calendar.time

            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            val startDate = calendar.time

            when (
                val result = getTransactionsUseCase(
                    accountId = 1,
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