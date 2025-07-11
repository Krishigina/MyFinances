package com.myfinances.ui.screens.history

import com.myfinances.ui.components.ListItemModel
import java.util.Date

/**
 * Определяет состояния UI для экрана "История".
 */
sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Content(
        val transactionItems: List<ListItemModel>,
        val totalAmount: Double,
        val currencyCode: String,
        val startDate: Date,
        val endDate: Date
    ) : HistoryUiState
}