package com.myfinances.ui.screens.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.domain.usecase.GetActiveAccountIdUseCase
import com.myfinances.domain.usecase.GetIncomeTransactionsUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.toSimpleListItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана "Доходы".
 * Отвечает за загрузку транзакций доходов, управление состоянием
 * экрана и взаимодействие с бизнес-логикой (UseCases).
 */
@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val getIncomeTransactionsUseCase: GetIncomeTransactionsUseCase,
    private val getActiveAccountIdUseCase: GetActiveAccountIdUseCase,
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

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = IncomeUiState.Loading

            when (val accountIdResult = getActiveAccountIdUseCase()) {
                is Result.Success -> {
                    val accountId = accountIdResult.data
                    when (val result = getIncomeTransactionsUseCase(accountId)) {
                        is Result.Success -> {
                            val transactions = result.data.first
                            val categories = result.data.second
                            val categoryMap = categories.associateBy { it.id }

                            val transactionItems = transactions.map { transaction ->
                                transaction.toSimpleListItemModel(categoryMap[transaction.categoryId])
                            }
                            val totalAmount = transactions.sumOf { it.amount }

                            _uiState.value = IncomeUiState.Success(
                                transactionItems = transactionItems,
                                totalAmount = totalAmount
                            )
                        }
                        is Result.Error -> {
                            _uiState.value =
                                IncomeUiState.Error(
                                    result.exception.message ?: "Неизвестная ошибка"
                                )
                        }
                        is Result.NetworkError -> {
                            _uiState.value = IncomeUiState.NoInternet
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.value = IncomeUiState.Error(
                        accountIdResult.exception.message ?: "Не удалось получить активный счет"
                    )
                }
                is Result.NetworkError -> {
                    _uiState.value = IncomeUiState.NoInternet
                }
            }
        }
    }
}