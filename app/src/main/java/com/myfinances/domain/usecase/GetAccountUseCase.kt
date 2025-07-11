package com.myfinances.domain.usecase

import com.myfinances.data.store.SessionStore
import com.myfinances.domain.entity.Account
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.util.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use-case для получения основного (единственного) счета пользователя.
 * Инкапсулирует бизнес-логику по извлечению первого счета из списка,
 * полученного от репозитория.
 */

class GetAccountUseCase @Inject constructor(
    private val repository: AccountsRepository,
    private val sessionStore: SessionStore
) {
    suspend operator fun invoke(): Result<Account> {
        val activeAccountId = sessionStore.activeAccountId.first()
            ?: return Result.Error(IllegalStateException("Активный счет не установлен"))

        return when (val accountsResult = repository.getAccounts()) {
            is Result.Success -> {
                val account = accountsResult.data.find { it.id == activeAccountId }
                if (account != null) {
                    Result.Success(account)
                } else {
                    Result.Error(Exception("Счет с ID $activeAccountId не найден"))
                }
            }

            is Result.Error -> accountsResult
            is Result.NetworkError -> accountsResult
        }
    }
}