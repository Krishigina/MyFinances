package com.myfinances.ui.screens.history

import com.myfinances.ui.components.ListItemModel
import java.util.Date

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Success(
        val transactionItems: List<ListItemModel>,
        val totalAmount: Double,
        val startDate: Date,
        val endDate: Date
    ) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
    data object NoInternet : HistoryUiState
}