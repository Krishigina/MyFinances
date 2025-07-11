package com.myfinances.ui.model

data class TransactionItemUiModel(
    val id: String,
    val title: String,
    val amountFormatted: String,
    val emoji: String,
    val subtitle: String?,
    val secondaryText: String? = null
)