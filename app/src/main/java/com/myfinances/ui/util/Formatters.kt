package com.myfinances.ui.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

fun getCurrencySymbol(currencyCode: String): String {
    return when (currencyCode.uppercase()) {
        "RUB" -> "₽"
        "USD" -> "$"
        "EUR" -> "€"
        else -> currencyCode
    }
}

fun formatCurrency(amount: Double, currencyCode: String): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale("ru", "RU")) as DecimalFormat
    val symbols = numberFormat.decimalFormatSymbols
    symbols.decimalSeparator = '.'
    numberFormat.decimalFormatSymbols = symbols
    numberFormat.minimumFractionDigits = 2
    numberFormat.maximumFractionDigits = 2

    val formattedAmount = numberFormat.format(amount).replace(" ", "\u00A0")
    val currencySymbol = getCurrencySymbol(currencyCode)

    return "$formattedAmount $currencySymbol"
}