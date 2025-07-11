package com.myfinances.ui.model

import java.util.Date

data class HistoryUiModel(
    val transactionItems: List<TransactionItemUiModel>,
    val totalAmount: Double,
    val currencyCode: String,
    val startDate: Date,
    val endDate: Date
)