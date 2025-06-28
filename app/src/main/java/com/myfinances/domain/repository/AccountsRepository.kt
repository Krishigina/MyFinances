package com.myfinances.domain.repository

import com.myfinances.domain.entity.Account
import com.myfinances.domain.util.Result

/**
 * Репозиторий для управления данными о счетах пользователя.
 */
interface AccountsRepository {
    suspend fun getAccounts(): Result<List<Account>>
}