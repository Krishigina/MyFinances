package com.myfinances.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.domain.usecase.GetAccountsUseCase
import com.myfinances.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
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
            when (val result = getAccountsUseCase()) {
                is Result.Success -> {
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