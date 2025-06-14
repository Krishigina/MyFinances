package com.myfinances.domain.entity

data class Account(
    val id: Int,
    val name: String,
    val balance: Double,
    val currency: String,
    val emoji: String
)