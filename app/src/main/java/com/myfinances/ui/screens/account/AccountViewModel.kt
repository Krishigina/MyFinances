package com.myfinances.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.domain.usecase.GetAccountUseCase
import com.myfinances.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * ViewModel для экрана "Счет".
 * Отвечает за загрузку данных о счете, управление состоянием экрана (загрузка, успех, ошибка)
 * и предоставление этих данных для отображения в UI.
 */
@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val connectivityManager: ConnectivityManagerSource
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccountUiState>(AccountUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        observeNetworkStatus()
        loadAccount()
    }

    private fun observeNetworkStatus() {
        connectivityManager.isNetworkAvailable
            .onEach { isAvailable ->
                if (isAvailable && _uiState.value is AccountUiState.NoInternet) {
                    loadAccount()
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadAccount() {
        viewModelScope.launch {
            _uiState.value = AccountUiState.Loading
            when (val result = getAccountUseCase()) {
                is Result.Success -> {
                    // Сразу получаем один счет
                    _uiState.value = AccountUiState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value =
                        AccountUiState.Error(result.exception.message ?: "Unknown error")
                }
                is Result.NetworkError -> {
                    _uiState.value = AccountUiState.NoInternet
                }
            }
        }
    }
}