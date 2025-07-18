package com.myfinances.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.myfinances.data.db.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Upsert
    suspend fun upsert(transaction: TransactionEntity)

    @Upsert
    suspend fun upsertAll(transactions: List<TransactionEntity>)

    @Query("SELECT * FROM transactions WHERE accountId = :accountId AND date BETWEEN :startDate AND :endDate AND isDeletedLocally = 0")
    fun getTransactionsForPeriod(accountId: Int, startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: Int): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE isSynced = 0")
    suspend fun getUnsyncedTransactions(): List<TransactionEntity>

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteById(transactionId: Int)
}