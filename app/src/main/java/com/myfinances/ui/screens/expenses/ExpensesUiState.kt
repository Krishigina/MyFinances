package com.myfinances.ui.screens.expenses

import com.myfinances.ui.model.TransactionItemUiModel

sealed interface ExpensesUiState {
    data object Loading : ExpensesUiState
    data class Content(
        val transactionItems: List<TransactionItemUiModel>,
        val totalAmountFormatted: String
    ) : ExpensesUiState
}