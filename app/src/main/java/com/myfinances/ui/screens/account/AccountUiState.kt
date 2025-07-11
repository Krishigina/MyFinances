package com.myfinances.ui.screens.account

import com.myfinances.ui.components.CurrencyModel
import com.myfinances.ui.model.AccountUiModel

sealed interface AccountUiState {
    data object Loading : AccountUiState
    data class Success(
        val account: AccountUiModel,
        val isEditMode: Boolean = false,
        val isSaving: Boolean = false,
        val draftName: String,
        val draftBalance: String,
        val draftCurrency: String,
        val showCurrencyPicker: Boolean = false,
        val availableCurrencies: List<CurrencyModel>
    ) : AccountUiState
}