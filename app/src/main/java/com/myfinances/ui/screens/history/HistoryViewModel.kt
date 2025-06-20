package com.myfinances.ui.screens.history

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.usecase.GetTransactionsUseCase
import com.myfinances.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val connectivityManager: ConnectivityManagerSource
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var startDate: Date
    private var endDate: Date

    init {
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
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = event.timestampMillis
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                startDate = calendar.time
                loadData()
            }
            is HistoryEvent.EndDateSelected -> {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = event.timestampMillis
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                endDate = calendar.time
                loadData()
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = HistoryUiState.Loading
            } finally {
                if (isActive) {
                    Log.d("TaskCancellation", "Coroutine for History finished successfully")
                } else {
                    Log.d("TaskCancellation", "Coroutine for History was cancelled")
                }
            }

            val filterType = savedStateHandle.get<TransactionTypeFilter>("transactionType")
                ?: TransactionTypeFilter.ALL

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

                is Result.NetworkError -> {
                    _uiState.value = HistoryUiState.NoInternet
                }
            }
        }
    }
}