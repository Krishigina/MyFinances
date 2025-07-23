package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Account
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.repository.SessionRepository
import com.myfinances.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class GetAccountUseCase(
    private val repository: AccountsRepository,
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<Result<Account>> {
        return sessionRepository.getActiveAccountId().flatMapLatest { activeId ->
            if (activeId == null) {
                kotlinx.coroutines.flow.flowOf(Result.Failure.GenericError(IllegalStateException("Активный счет не установлен")))
            } else {
                repository.getAccounts().map { accounts ->
                    val account = accounts.find { it.id == activeId }
                    if (account != null) {
                        Result.Success(account)
                    } else {
                        Result.Failure.GenericError(Exception("Счет с ID $activeId не найден"))
                    }
                }
            }
        }
    }

    suspend fun refresh(): Result<Unit> {
        return repository.refreshAccounts()
    }
}