package com.myfinances.domain.repository

import com.myfinances.domain.entity.Account
import com.myfinances.domain.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для управления данными о счетах пользователя.
 */
interface AccountsRepository {
    fun getAccounts(): Flow<List<Account>>

    suspend fun refreshAccounts(): Result<Unit>

    suspend fun updateAccount(
        accountId: Int,
        name: String,
        balance: Double,
        currency: String
    ): Result<Account>
}