package com.myfinances.data.network.dto

import com.google.gson.annotations.SerializedName
import com.myfinances.domain.entity.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
    data class AccountInfo(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String
    )

    data class CategoryInfo(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("emoji") val emoji: String?
    )
}

fun TransactionDto.toDomainModel(): Transaction {
    val finalCategoryId = this.category?.id ?: this.categoryId ?: -1

    return Transaction(
        id = this.id,
        categoryId = finalCategoryId,
        amount = this.amount,
        comment = this.comment,
        date = parseDate(this.transactionDate)
    )
}

private fun parseDate(dateString: String): Date {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("UTC")
    return try {
        format.parse(dateString) ?: Date()
    } catch (e: Exception) {
        e.printStackTrace()
        Date()
    }
}