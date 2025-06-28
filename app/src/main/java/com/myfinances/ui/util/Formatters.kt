package com.myfinances.ui.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

/**
 * Форматирует числовое значение в строку валюты согласно российским стандартам.
 * Заменяет стандартный пробел на неразрывный для корректного отображения.
 *
 * @param amount Сумма для форматирования.
 * @return Строка вида "1 234,56 ₽".
 */
fun formatCurrency(amount: Double): String {
    val numberFormat = NumberFormat.getCurrencyInstance(Locale("ru", "RU")) as DecimalFormat
    val symbols = numberFormat.decimalFormatSymbols
    symbols.decimalSeparator = '.'
    numberFormat.decimalFormatSymbols = symbols
    numberFormat.minimumFractionDigits = 2
    numberFormat.maximumFractionDigits = 2

    return numberFormat.format(amount).replace(" ", "\u00A0")
}