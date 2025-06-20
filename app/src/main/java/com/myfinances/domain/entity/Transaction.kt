package com.myfinances.domain.entity

import java.util.Date

data class Transaction(
    val id: Int,
    val categoryId: Int,
    val amount: String,
    val comment: String? = null,
    val date: Date
)