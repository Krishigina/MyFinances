package com.myfinances.ui.model

data class AccountUiModel(
    val id: Int,
    val name: String,
    val balance: Double,
    val currency: String,
    val emoji: String,
    val balanceFormatted: String,
    val currencySymbol: String
)