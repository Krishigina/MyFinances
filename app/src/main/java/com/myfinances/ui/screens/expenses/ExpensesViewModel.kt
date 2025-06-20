package com.myfinances.ui.screens.expenses

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.domain.usecase.GetExpenseTransactionsUseCase
import com.myfinances.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val getExpenseTransactionsUseCase: GetExpenseTransactionsUseCase,
    private val connectivityManager: ConnectivityManagerSource
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExpensesUiState>(ExpensesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        observeNetworkStatus()
        loadData()
    }

    private fun observeNetworkStatus() {
        connectivityManager.isNetworkAvailable
            .onEach { isAvailable ->
                if (isAvailable && _uiState.value is ExpensesUiState.NoInternet) {
                    loadData()
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = ExpensesUiState.Loading
            } finally {
                if (isActive) {
                    Log.d("TaskCancellation", "Coroutine for Expenses finished successfully")
                } else {
                    Log.d("TaskCancellation", "Coroutine for Expenses was cancelled")
                }
            }

            when (val result = getExpenseTransactionsUseCase(accountId = 1)) {
                is Result.Success -> {
                    _uiState.value = ExpensesUiState.Success(
                        transactions = result.data.first,
                        categories = result.data.second
                    )
                }
                is Result.Error -> {
                    _uiState.value =
                        ExpensesUiState.Error(result.exception.message ?: "Unknown error")
                }

                is Result.NetworkError -> {
                    _uiState.value = ExpensesUiState.NoInternet
                }
            }
        }
    }
}