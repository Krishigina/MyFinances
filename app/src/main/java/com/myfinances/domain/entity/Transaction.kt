package com.myfinances.domain.entity

import java.util.Date

/**
 * Представляет сущность "Транзакция" в доменном слое.
 * Содержит всю необходимую информацию о конкретной финансовой операции,
 * включая сумму, дату, комментарий и принадлежность к категории.
 */
data class Transaction(
    val id: Int,
    val accountId: Int,
    val categoryId: Int? = null,
    val amount: Double,
    val comment: String,
    val date: Date
)