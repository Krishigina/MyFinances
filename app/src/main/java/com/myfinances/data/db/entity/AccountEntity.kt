package com.myfinances.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.myfinances.domain.entity.Account

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val balance: Double,
    val currency: String,
    val isSynced: Boolean = true,
    val lastUpdatedAt: Long
)
    {
        fun toDomainModel(emoji: String = "💰"): Account {
            return Account(
                id = this.id,
                name = this.name,
                balance = this.balance,
                currency = this.currency,
                emoji = emoji,
                lastUpdatedAt = this.lastUpdatedAt
            )
        }
    }

    fun Account.toEntity(isSynced: Boolean = true): AccountEntity {
        return AccountEntity(
            id = this.id,
            name = this.name,
            balance = this.balance,
            currency = this.currency,
            isSynced = isSynced,
            lastUpdatedAt = this.lastUpdatedAt
        )
    }