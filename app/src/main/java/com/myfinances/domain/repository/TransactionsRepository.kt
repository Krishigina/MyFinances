package com.myfinances.domain.repository

import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.util.Result
import java.util.Date

/**
 * Репозиторий для управления данными о транзакциях.
 */
interface TransactionsRepository {
    suspend fun getTransactions(
        accountId: Int,
        startDate: Date,
        endDate: Date
    ): Result<List<Transaction>>

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
}