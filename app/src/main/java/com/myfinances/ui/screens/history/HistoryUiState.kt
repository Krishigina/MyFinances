package com.myfinances.ui.screens.history

import com.myfinances.ui.model.TransactionItemUiModel
import java.util.Date

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Content(
        val transactionItems: List<TransactionItemUiModel>,
        val totalAmount: Double,
        val currencyCode: String,
        val startDate: Date,
        val endDate: Date
    ) : HistoryUiState
}