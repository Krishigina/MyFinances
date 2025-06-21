package com.myfinances.ui.screens.account

import com.myfinances.domain.entity.Account

sealed interface AccountUiState {
    data object Loading : AccountUiState
    data class Success(val account: Account) : AccountUiState
    data class Error(val message: String) : AccountUiState
    data object NoInternet : AccountUiState
}