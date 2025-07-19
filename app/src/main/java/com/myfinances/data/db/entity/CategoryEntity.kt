package com.myfinances.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.myfinances.domain.entity.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val emoji: String?,
    val isIncome: Boolean
) {
    fun toDomainModel(): Category {
        return Category(
            id = this.id,
            name = this.name,
            emoji = this.emoji,
            isIncome = this.isIncome
        )
    }
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        name = this.name,
        emoji = this.emoji,
        isIncome = this.isIncome
    )
}