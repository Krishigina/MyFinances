package com.myfinances.data.network.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO для запроса на обновление счета методом PUT.
 * Содержит все поля, необходимые для полного обновления счета.
 */
data class UpdateAccountRequest(
    @SerializedName("name") val name: String,
    @SerializedName("balance") val balance: String,
    @SerializedName("currency") val currency: String
)