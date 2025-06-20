package com.myfinances.ui.screens.common

import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction

sealed interface TransactionsUiState {
    data object Loading : TransactionsUiState
    data class Success(
        val transactions: List<Transaction>,
        val categories: List<Category>
    ) : TransactionsUiState

    data class Error(val message: String) : TransactionsUiState
}