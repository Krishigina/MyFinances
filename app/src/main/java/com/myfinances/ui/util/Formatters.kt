package com.myfinances.ui.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

/**
 * Форматирует числовое значение в строку с указанием валюты.
 * Заменяет стандартный пробел на неразрывный для корректного отображения.
 *
 * @param amount Сумма для форматирования.
 * @param currencyCode ISO 4217 код валюты (e.g., "RUB", "USD").
 * @return Строка вида "1 234,56 ₽".
 */
fun formatCurrency(amount: Double, currencyCode: String): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale("ru", "RU")) as DecimalFormat
    val symbols = numberFormat.decimalFormatSymbols
    symbols.decimalSeparator = '.'
    numberFormat.decimalFormatSymbols = symbols
    numberFormat.minimumFractionDigits = 2
    numberFormat.maximumFractionDigits = 2

    val formattedAmount = numberFormat.format(amount).replace(" ", "\u00A0")

    val currencySymbol = when (currencyCode.uppercase()) {
        "RUB" -> "₽"
        "USD" -> "$"
        "EUR" -> "€"
        else -> currencyCode
    }

    return "$formattedAmount $currencySymbol"
}