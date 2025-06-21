package com.myfinances.ui.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(amount: Double): String {
    val numberFormat = NumberFormat.getCurrencyInstance(Locale("ru", "RU")) as DecimalFormat
    val symbols = numberFormat.decimalFormatSymbols
    symbols.decimalSeparator = '.'
    numberFormat.decimalFormatSymbols = symbols
    numberFormat.minimumFractionDigits = 2
    numberFormat.maximumFractionDigits = 2

    return numberFormat.format(amount).replace(" ", "\u00A0")
}