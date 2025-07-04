package com.myfinances.ui.screens.history

import com.myfinances.ui.components.ListItemModel
import java.util.Date

/**
 * Определяет состояния UI для экрана "История".
 */
sealed interface HistoryUiState {
    /**
     * Состояние загрузки данных.
     */
    data object Loading : HistoryUiState

    /**
     * Успешное состояние, когда данные загружены.
     * @param transactionItems Список моделей транзакций для отображения.
     * @param totalAmount Общая сумма транзакций за выбранный период.
     * @param startDate Начальная дата периода.
     * @param endDate Конечная дата периода.
     * @param currency Валюта счета для корректного отображения сумм.
     */
    data class Success(
        val transactionItems: List<ListItemModel>,
        val totalAmount: Double,
        val startDate: Date,
        val endDate: Date,
        val currency: String
    ) : HistoryUiState

    /**
     * Состояние ошибки.
     * @param message Сообщение об ошибке.
     */
    data class Error(val message: String) : HistoryUiState

    /**
     * Состояние отсутствия сети.
     */
    data object NoInternet : HistoryUiState
}