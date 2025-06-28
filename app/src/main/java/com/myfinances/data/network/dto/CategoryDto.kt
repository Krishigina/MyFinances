package com.myfinances.data.network.dto

import com.google.gson.annotations.SerializedName
import com.myfinances.domain.entity.Category

/**
 * Data Transfer Object для сущности "Категория".
 * Представляет структуру данных о категории, как она приходит от бэкенда.
 * Используется для парсинга сетевого ответа.
 */
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