package com.myfinances.domain.entity

/**
 * Представляет сущность "Счет" в доменном слое приложения.
 * Этот класс является чистой моделью данных, содержащей основную
 * информацию о финансовом счете пользователя, такую как баланс, валюта и название.
 */
data class Account(
    val id: Int,
    val name: String,
    val balance: Double,
    val currency: String,
    val emoji: String,
    val lastUpdatedAt: Long
)