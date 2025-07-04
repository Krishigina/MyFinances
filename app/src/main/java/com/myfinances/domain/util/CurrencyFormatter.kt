package com.myfinances.ui.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/**
 * Утилитарная функция для форматирования денежных сумм.
 *
 * Преобразует числовое значение в локализованную строку с символом валюты,
 * используя стандартный `NumberFormat`.
 *
 * @param amount Сумма для форматирования.
 * @param currencyCode ISO 4217 код валюты (например, "RUB", "USD", "EUR").
 * @return Отформатированная строка вида "1 234,56 ₽". Если код валюты не поддерживается
 * системным `java.util.Currency`, функция вернет сумму с кодом валюты в конце
 * (например, "1 234,56 KZT"). Для корректного отображения стандартный пробел
 * заменяется на неразрывный (`\u00A0`).
 */
fun formatCurrency(amount: Double, currencyCode: String): String {
    return try {
        val format = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
        format.currency = Currency.getInstance(currencyCode.uppercase(Locale.ROOT))
        format.format(amount).replace(" ", "\u00A0")
    } catch (e: IllegalArgumentException) {
        val numberFormat = NumberFormat.getNumberInstance(Locale("ru", "RU"))
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2
        "${
            numberFormat.format(amount).replace(" ", "\u00A0")
        } ${currencyCode.uppercase(Locale.ROOT)}"
    }
}