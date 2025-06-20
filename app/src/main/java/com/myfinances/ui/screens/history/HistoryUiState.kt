package com.myfinances.ui.screens.history

import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction
import java.util.Date

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Success(
        val transactions: List<Transaction>,
        val categories: List<Category>,
        val startDate: Date,
        val endDate: Date
    ) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
    data object NoInternet : HistoryUiState
}