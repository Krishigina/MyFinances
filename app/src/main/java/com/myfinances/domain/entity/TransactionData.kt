package com.myfinances.domain.entity

import java.util.Date

/**
 * Простой data-класс, который служит контейнером для данных,
 * подготовленных UseCase'ом для дальнейшей передачи в ViewModel.
 * Он не содержит логики, только данные доменного слоя.
 */
data class TransactionData(
    val transactions: List<Transaction>,
    val categories: Map<Int, Category>,
    val account: Account,
    val totalAmount: Double,
    val startDate: Date,
    val endDate: Date
)