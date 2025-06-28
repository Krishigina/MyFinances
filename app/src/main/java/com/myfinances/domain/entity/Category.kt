package com.myfinances.domain.entity

/**
 * Представляет сущность "Категория" в доменном слое.
 * Описывает категорию, к которой может относиться транзакция,
 * и определяет, является ли она доходной или расходной.
 */
data class Category(
    val id: Int,
    val name: String,
    val emoji: String? = null,
    val isIncome: Boolean
)