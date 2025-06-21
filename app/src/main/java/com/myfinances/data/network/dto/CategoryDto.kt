package com.myfinances.data.network.dto

import com.google.gson.annotations.SerializedName
import com.myfinances.domain.entity.Category

data class CategoryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("emoji") val emoji: String?,
    @SerializedName("isIncome") val isIncome: Boolean
)

fun CategoryDto.toDomainModel(): Category {
    return Category(
        id = this.id,
        name = this.name,
        emoji = this.emoji,
        isIncome = this.isIncome
    )
}