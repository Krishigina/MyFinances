package com.myfinances.ui.screens.account

import com.myfinances.domain.entity.Account
import com.myfinances.ui.components.CurrencyModel

/**
 * Определяет все возможные состояния UI для экрана "Счет".
 */

sealed interface AccountUiState {
    data object Loading : AccountUiState
    data class Success(
        val account: Account,
        val isEditMode: Boolean = false,
        val isSaving: Boolean = false,
        val draftName: String,
        val draftBalance: String,
        val draftCurrency: String,
        val showCurrencyPicker: Boolean = false,
        val availableCurrencies: List<CurrencyModel>
    ) : AccountUiState
}