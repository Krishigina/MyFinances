package com.myfinances.data.network.dto

import com.google.gson.annotations.SerializedName
import com.myfinances.domain.entity.Transaction
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Data Transfer Object для сущности "Транзакция".
 * Отражает сложную структуру JSON-объекта транзакции от API, включая
 * вложенные объекты с информацией о счете и категории. Предназначен
 * исключительно для передачи данных из сетевого слоя.
 */
data class TransactionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("account") val account: AccountInfo?,
    @SerializedName("category") val category: CategoryInfo?,
    @SerializedName("accountId") val accountId: Int?,
    @SerializedName("categoryId") val categoryId: Int?,
    @SerializedName("amount") val amount: String,
    @SerializedName("transactionDate") val transactionDate: String,
    @SerializedName("comment") val comment: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
) {
    /**
     * Вложенный DTO с краткой информацией о счете.
     */
    data class AccountInfo(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String
    )

    /**
     * Вложенный DTO с краткой информацией о категории.
     */
    data class CategoryInfo(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("emoji") val emoji: String?
    )
}

fun TransactionDto.toDomainModel(): Transaction? {
    val finalCategoryId = this.category?.id ?: this.categoryId
    val finalAccountId = this.account?.id ?: this.accountId ?: return null
    val parsedDate = parseDate(this.transactionDate) ?: return null
    val parsedUpdatedAt = this.updatedAt?.let { parseTimestamp(it) } ?: parsedDate.time

    return Transaction(
        id = this.id,
        accountId = finalAccountId,
        categoryId = finalCategoryId,
        amount = this.amount.toDoubleOrNull() ?: 0.0,
        comment = this.comment ?: "",
        date = parsedDate,
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

private fun parseDate(dateString: String): Date? {
    val formats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    )
    formats.forEach { it.timeZone = TimeZone.getTimeZone("UTC") }

    for (format in formats) {
        try {
            return format.parse(dateString)
        } catch (e: ParseException) {
            // Игнорируем ошибку и пробуем следующий формат
        }
    }
    return null
}