package com.myfinances.ui.screens.expenses

import com.myfinances.ui.components.ListItemModel

sealed interface ExpensesUiState {
    data object Loading : ExpensesUiState
    data class Success(
        val transactionItems: List<ListItemModel>,
        val totalAmount: Double
    ) : ExpensesUiState
    data class Error(val message: String) : ExpensesUiState
    data object NoInternet : ExpensesUiState
}