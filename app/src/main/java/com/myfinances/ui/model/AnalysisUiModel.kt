package com.myfinances.ui.model

import java.util.Date

/**
 * UI-модель для экрана "Анализ".
 *
 * @param categorySpents Список трат/доходов по категориям.
 * @param totalAmountFormatted Отформатированная общая сумма.
 * @param startDate Начальная дата периода.
 * @param endDate Конечная дата периода.
 */
data class AnalysisUiModel(
    val categorySpents: List<CategorySpendingUiModel>,
    val totalAmountFormatted: String,
    val startDate: Date,
    val endDate: Date
)