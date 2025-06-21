package com.myfinances.domain.usecase

import com.myfinances.data.store.SessionStore
import com.myfinances.domain.repository.MyFinancesRepository
import com.myfinances.domain.util.Result
import javax.inject.Inject

class GetActiveAccountIdUseCase @Inject constructor(
    private val repository: MyFinancesRepository,
    private val sessionStore: SessionStore
) {
    suspend operator fun invoke(): Result<Int> {
        val cachedAccountId = sessionStore.getAccountId()
        if (cachedAccountId != null) {
            return Result.Success(cachedAccountId)
        }

        return when (val accountsResult = repository.getAccounts()) {
            is Result.Success -> {
                val firstAccountId = accountsResult.data.firstOrNull()?.id
                if (firstAccountId != null) {
                    sessionStore.setAccountId(firstAccountId)
                    Result.Success(firstAccountId)
                } else {
                    Result.Error(Exception("User has no accounts"))
                }
            }

            is Result.Error -> accountsResult
            is Result.NetworkError -> accountsResult
        }
    }
}