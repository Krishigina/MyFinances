package com.myfinances.ui.model

import androidx.compose.ui.graphics.Color

/**
 * UI-модель, представляющая агрегированные данные по одной категории для отображения.
 *
 * @param id Уникальный идентификатор категории.
 * @param title Название категории.
 * @param emoji Эмодзи категории.
 * @param amountFormatted Отформатированная сумма трат/доходов.
 * @param percentage Процент от общей суммы.
 * @param color Цвет для графика.
 * @param topTransactionId ID самой крупной транзакции в этой категории для навигации.
 */
data class CategorySpendingUiModel(
    val id: String,
    val title: String,
    val emoji: String,
    val amountFormatted: String,
    val percentage: Int,
    val color: Color,
    val topTransactionId: Int?
)