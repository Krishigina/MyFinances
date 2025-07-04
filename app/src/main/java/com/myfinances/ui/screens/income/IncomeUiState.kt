package com.myfinances.ui.screens.income

import com.myfinances.ui.components.ListItemModel

/**
 * Определяет состояния UI для экрана "Доходы".
 */
sealed interface IncomeUiState {
    /**
     * Состояние загрузки данных.
     */
    data object Loading : IncomeUiState

    /**
     * Успешное состояние, когда данные загружены.
     * @param transactionItems Список моделей транзакций для отображения.
     * @param totalAmount Общая сумма доходов за период.
     * @param currency Валюта счета для корректного отображения сумм.
     */
    data class Success(
        val transactionItems: List<ListItemModel>,
        val totalAmount: Double,
        val currency: String
    ) : IncomeUiState

    /**
     * Состояние ошибки.
     * @param message Сообщение об ошибке.
     */
    data class Error(val message: String) : IncomeUiState

    /**
     * Состояние отсутствия сети.
     */
    data object NoInternet : IncomeUiState
}