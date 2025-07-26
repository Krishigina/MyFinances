package com.myfinances.ui.model


data class CategorySpendingUiModel(
    val id: String,
    val title: String,
    val emoji: String,
    val amountFormatted: String,
    val percentage: Int,
    val topTransactionId: Int?
)