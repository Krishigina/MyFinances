package com.myfinances.ui.screens.income

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.domain.usecase.GetIncomeTransactionsUseCase
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
class IncomeViewModel @Inject constructor(
    private val getIncomeTransactionsUseCase: GetIncomeTransactionsUseCase,
    private val connectivityManager: ConnectivityManagerSource
) : ViewModel() {

    private val _uiState = MutableStateFlow<IncomeUiState>(IncomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        observeNetworkStatus()
        loadData()
    }

    private fun observeNetworkStatus() {
        connectivityManager.isNetworkAvailable
            .onEach { isAvailable ->
                if (isAvailable && _uiState.value is IncomeUiState.NoInternet) {
                    loadData()
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = IncomeUiState.Loading
            } finally {
                if (isActive) {
                    Log.d("TaskCancellation", "Coroutine for Income finished successfully")
                } else {
                    Log.d("TaskCancellation", "Coroutine for Income was cancelled")
                }
            }

            when (val result = getIncomeTransactionsUseCase(accountId = 1)) {
                is Result.Success -> {
                    _uiState.value = IncomeUiState.Success(
                        transactions = result.data.first,
                        categories = result.data.second
                    )
                }
                is Result.Error -> {
                    _uiState.value =
                        IncomeUiState.Error(result.exception.message ?: "Unknown error")
                }

                is Result.NetworkError -> {
                    _uiState.value = IncomeUiState.NoInternet
                }
            }
        }
    }
}