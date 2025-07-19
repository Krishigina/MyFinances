package com.myfinances.domain.entity

import java.util.Date

/**
 * Простой data-класс, который служит контейнером для данных анализа,
 * подготовленных UseCase'ом для дальнейшей передачи в ViewModel.
 *
 * @param categorySpents Список агрегированных данных по категориям.
 * @param totalAmount Общая сумма за период.
 * @param account Аккаунт, по которому проводится анализ.
 * @param startDate Начальная дата периода.
 * @param endDate Конечная дата периода.
 */
data class AnalysisData(
    val categorySpents: List<CategorySpending>,
    val totalAmount: Double,
    val account: Account,
    val startDate: Date,
    val endDate: Date
)