package com.myfinances.domain.entity


data class Category(
    val id: Int,
    val name: String,
    val emoji: String,
    val isIncome: Boolean
)