package com.myfinances.ui.screens.income

import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction

sealed interface IncomeUiState {
    data object Loading : IncomeUiState
    data class Success(
        val transactions: List<Transaction>,
        val categories: List<Category>
    ) : IncomeUiState

    data class Error(val message: String) : IncomeUiState
}