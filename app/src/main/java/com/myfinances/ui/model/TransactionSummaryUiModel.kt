package com.myfinances.ui.model

data class TransactionSummaryUiModel(
    val transactionItems: List<TransactionItemUiModel>,
    val totalAmountFormatted: String
)