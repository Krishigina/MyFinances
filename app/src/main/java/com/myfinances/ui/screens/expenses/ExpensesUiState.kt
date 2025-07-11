package com.myfinances.ui.screens.expenses

import com.myfinances.ui.components.ListItemModel

/**
 * Определяет состояния UI для экрана "Расходы".
 */
sealed interface ExpensesUiState {
    data object Loading : ExpensesUiState
    data class Content(
        val transactionItems: List<ListItemModel>,
        val totalAmountFormatted: String
    ) : ExpensesUiState
}