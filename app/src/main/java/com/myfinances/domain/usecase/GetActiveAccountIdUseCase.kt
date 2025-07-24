package com.myfinances.domain.usecase

import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.repository.SessionRepository
import com.myfinances.domain.util.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetActiveAccountIdUseCase @Inject constructor(
    private val repository: AccountsRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(): Result<Int> {
        val cachedAccountId = sessionRepository.getActiveAccountId().first()
        if (cachedAccountId != null) {
            return Result.Success(cachedAccountId)
        }

        val accountsFromDb = repository.getAccounts().first()
        val firstAccountId = accountsFromDb.firstOrNull()?.id

        return if (firstAccountId != null) {
            sessionRepository.setActiveAccountId(firstAccountId)
            Result.Success(firstAccountId)
        } else {
            val refreshResult = repository.refreshAccounts()
            if (refreshResult is Result.Success) {
                val accountsAfterRefresh = repository.getAccounts().first()
                val newFirstAccountId = accountsAfterRefresh.firstOrNull()?.id
                if (newFirstAccountId != null) {
                    sessionRepository.setActiveAccountId(newFirstAccountId)
                    Result.Success(newFirstAccountId)
                } else {
                    Result.Failure.GenericError(Exception("У пользователя нет счетов после обновления"))
                }
            } else {
                Result.Failure.GenericError(Exception("Не удалось получить аккаунты ни локально, ни с сервера"))
            }
        }
    }
}