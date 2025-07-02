package com.myfinances.ui.screens.income

import com.myfinances.ui.components.ListItemModel

sealed interface IncomeUiState {
    data object Loading : IncomeUiState
    data class Success(
        val transactionItems: List<ListItemModel>,
        val totalAmount: Double
    ) : IncomeUiState
    data class Error(val message: String) : IncomeUiState
    data object NoInternet : IncomeUiState
}