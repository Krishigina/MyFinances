package com.myfinances.domain.entity

/**
 * Представляет агрегированные данные по одной категории за период.
 *
 * @param category Детали категории.
 * @param amount Сумма транзакций по этой категории.
 * @param transactionsCount Количество транзакций.
 */
data class CategorySpending(
    val category: Category,
    val amount: Double,
    val transactionsCount: Int
)