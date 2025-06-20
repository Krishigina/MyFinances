package com.myfinances.domain.repository

import com.myfinances.domain.entity.Account
import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.util.Result
import java.util.Date

interface MyFinancesRepository {
    suspend fun getTransactions(
        accountId: Int,
        startDate: Date,
        endDate: Date
    ): Result<List<Transaction>>

    suspend fun getCategories(): Result<List<Category>>
    suspend fun getAccounts(): Result<List<Account>>
}