package com.myfinances.data.network.dto

import com.google.gson.annotations.SerializedName
import com.myfinances.domain.entity.Account
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.Date

/**
 * Data Transfer Object для сущности "Счет".
 * Этот класс является точным представлением JSON-объекта, получаемого от API.
 * Его основная задача — служить контейнером для данных при десериализации
 * ответа сервера с помощью библиотеки Gson.
 */
data class AccountDto(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("balance") val balance: String,
    @SerializedName("currency") val currency: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

fun AccountDto.toDomainModel(): Account {
    val parsedUpdatedAt = this.updatedAt.let { parseTimestamp(it) } ?: Date().time

    return Account(
        id = this.id,
        name = this.name,
        balance = this.balance.toDoubleOrNull() ?: 0.0,
        currency = this.currency,
        emoji = "💰",
        lastUpdatedAt = parsedUpdatedAt
    )
}

private fun parseTimestamp(dateString: String): Long? {
    return try {
        Instant.parse(dateString).toEpochMilli()
    } catch (e: DateTimeParseException) {
        null
    }
}