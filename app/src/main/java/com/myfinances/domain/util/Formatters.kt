package com.myfinances.ui.util

import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    format.maximumFractionDigits = 0
    return format.format(amount).replace(" ", "\u00A0")
}