package com.myfinances.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.myfinances.domain.entity.Transaction
import java.util.Date

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["accountId"]), Index(value = ["categoryId"])]
)
data class TransactionEntity(
    @PrimaryKey val id: Int,
    val accountId: Int,
    val categoryId: Int?,
    val amount: Double,
    val comment: String,
    val date: Date,
    val isSynced: Boolean = true,
    val isDeletedLocally: Boolean = false,
    val lastUpdatedAt: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): Transaction {
        return Transaction(
            id = this.id,
            accountId = this.accountId,
            categoryId = this.categoryId,
            amount = this.amount,
            comment = this.comment,
            date = this.date,
            lastUpdatedAt = this.lastUpdatedAt
        )
    }
}

fun Transaction.toEntity(isSynced: Boolean = true, isDeletedLocally: Boolean = false): TransactionEntity {
    return TransactionEntity(
        id = this.id,
        accountId = this.accountId,
        categoryId = this.categoryId,
        amount = this.amount,
        comment = this.comment,
        date = this.date,
        isSynced = isSynced,
        isDeletedLocally = isDeletedLocally,
        lastUpdatedAt = this.lastUpdatedAt
    )
}