package com.myfinances.domain.repository

import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.util.Result
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface TransactionsRepository {
    fun getTransactions(
        accountId: Int,
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>>

    suspend fun refreshTransactions(
        accountId: Int,
        startDate: Date,
        endDate: Date
    ): Result<Unit>

    suspend fun getTransactionById(transactionId: Int): Result<Transaction>

    suspend fun createTransaction(
        accountId: Int,
        categoryId: Int,
        amount: Double,
        transactionDate: Date,
        comment: String
    ): Result<Transaction>

    suspend fun updateTransaction(
        transactionId: Int,
        accountId: Int,
        categoryId: Int,
        amount: Double,
        transactionDate: Date,
        comment: String
    ): Result<Transaction>

    suspend fun deleteTransaction(transactionId: Int): Result<Unit>

    fun scheduleSync()
}