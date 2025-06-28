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
}