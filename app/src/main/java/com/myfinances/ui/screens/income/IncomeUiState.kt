package com.myfinances.ui.screens.income

import com.myfinances.ui.components.ListItemModel

/**
 * Определяет состояния UI для экрана "Доходы".
 */
sealed interface IncomeUiState {
    data object Loading : IncomeUiState
    data class Content(
        val transactionItems: List<ListItemModel>,
        val totalAmountFormatted: String
    ) : IncomeUiState
}