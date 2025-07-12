package com.myfinances.data.network.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO для запроса на обновление транзакции методом PUT.
 * Структура идентична запросу на создание.
 */
data class UpdateTransactionRequest(
    @SerializedName("accountId") val accountId: Int,
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("amount") val amount: String,
    @SerializedName("transactionDate") val transactionDate: String,
    @SerializedName("comment") val comment: String
)