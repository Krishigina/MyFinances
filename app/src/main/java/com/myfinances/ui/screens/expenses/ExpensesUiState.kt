package com.myfinances.ui.screens.expenses

import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction

sealed interface ExpensesUiState {
    data object Loading : ExpensesUiState
    data class Success(
        val transactions: List<Transaction>,
        val categories: List<Category>
    ) : ExpensesUiState

    data class Error(val message: String) : ExpensesUiState
}