package com.myfinances.ui.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.domain.usecase.GetActiveAccountIdUseCase
import com.myfinances.domain.usecase.GetExpenseTransactionsUseCase
import com.myfinances.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана "Расходы".
 * Отвечает за загрузку транзакций расходов за текущий месяц, управление состоянием
 * экрана и взаимодействие с бизнес-логикой (UseCases).
 */
@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val getExpenseTransactionsUseCase: GetExpenseTransactionsUseCase,
    private val getActiveAccountIdUseCase: GetActiveAccountIdUseCase,
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
            _uiState.value = ExpensesUiState.Loading

            when (val accountIdResult = getActiveAccountIdUseCase()) {
                is Result.Success -> {
                    val accountId = accountIdResult.data
                    when (val result = getExpenseTransactionsUseCase(accountId)) {
                        is Result.Success -> {
                            _uiState.value = ExpensesUiState.Success(
                                transactions = result.data.first,
                                categories = result.data.second
                            )
                        }

                        is Result.Error -> {
                            _uiState.value =
                                ExpensesUiState.Error(
                                    result.exception.message ?: "Неизвестная ошибка"
                                )
                        }

                        is Result.NetworkError -> {
                            _uiState.value = ExpensesUiState.NoInternet
                        }
                    }
                }

                is Result.Error -> {
                    _uiState.value = ExpensesUiState.Error(
                        accountIdResult.exception.message ?: "Не удалось получить активный счет"
                    )
                }
                is Result.NetworkError -> {
                    _uiState.value = ExpensesUiState.NoInternet
                }
            }
        }
    }
}