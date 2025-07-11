package com.myfinances.ui.screens.income

import com.myfinances.ui.model.TransactionItemUiModel

sealed interface IncomeUiState {
    data object Loading : IncomeUiState
    data class Content(
        val transactionItems: List<TransactionItemUiModel>,
        val totalAmountFormatted: String
    ) : IncomeUiState
}