package com.myfinances.ui.screens.expenses

import com.myfinances.ui.components.ListItemModel

/**
 * Определяет состояния UI для экрана "Расходы".
 */
sealed interface ExpensesUiState {
    /**
     * Состояние загрузки данных.
     */
    data object Loading : ExpensesUiState

    /**
     * Успешное состояние, когда данные загружены.
     * @param transactionItems Список моделей транзакций для отображения.
     * @param totalAmount Общая сумма расходов за период.
     * @param currency Валюта счета для корректного отображения сумм.
     */
    data class Success(
        val transactionItems: List<ListItemModel>,
        val totalAmount: Double,
        val currency: String
    ) : ExpensesUiState

    /**
     * Состояние ошибки.
     * @param message Сообщение об ошибке.
     */
    data class Error(val message: String) : ExpensesUiState

    /**
     * Состояние отсутствия сети.
     */
    data object NoInternet : ExpensesUiState
}