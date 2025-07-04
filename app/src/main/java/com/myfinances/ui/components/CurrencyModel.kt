package com.myfinances.ui.components

/**
 * UI-модель, представляющая валюту.
 * Используется для отображения в BottomSheet выбора валюты.
 *
 * @param code ISO 4217 код валюты (e.g., "RUB", "USD").
 * @param name Полное название валюты (e.g., "Российский рубль").
 * @param symbol Символ валюты (e.g., "₽", "$").
 */
data class CurrencyModel(
    val code: String,
    val name: String,
    val symbol: String
)