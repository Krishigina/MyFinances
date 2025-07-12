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

        return when (val accountsResult = repository.getAccounts()) {
            is Result.Success -> {
                val firstAccountId = accountsResult.data.firstOrNull()?.id
                if (firstAccountId != null) {
                    sessionRepository.setActiveAccountId(firstAccountId)
                    Result.Success(firstAccountId)
                } else {
                    Result.Error(Exception("У пользователя нет счетов"))
                }
            }
            is Result.Error -> accountsResult
            is Result.NetworkError -> accountsResult
        }
    }
}