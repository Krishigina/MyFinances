package com.myfinances.domain.entity

data class Transaction(
    val id: Int,
    val categoryId: Int,
    val amount: String,
    val comment: String? = null
)