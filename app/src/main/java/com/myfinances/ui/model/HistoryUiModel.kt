package com.myfinances.ui.model

import com.myfinances.ui.components.ListItemModel
import java.util.Date

/**
 * UI-модель для экрана "История".
 * Включает в себя данные из TransactionSummaryUiModel и дополнительно даты периода.
 */
data class HistoryUiModel(
    val transactionItems: List<ListItemModel>,
    val totalAmount: Double,
    val currencyCode: String,
    val startDate: Date,
    val endDate: Date
)