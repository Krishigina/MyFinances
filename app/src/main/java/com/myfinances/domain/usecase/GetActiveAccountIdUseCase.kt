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
        repository.refreshAccounts()
        val accountsFromDb = repository.getAccounts().first()
        val firstAccountId = accountsFromDb.firstOrNull()?.id

        return if (firstAccountId != null) {
            sessionRepository.setActiveAccountId(firstAccountId)
            Result.Success(firstAccountId)
        } else {
            Result.Failure.GenericError(Exception("У пользователя нет счетов"))
        }
    }
}