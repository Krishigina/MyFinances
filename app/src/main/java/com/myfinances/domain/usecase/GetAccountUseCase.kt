package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Account
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.repository.SessionRepository
import com.myfinances.domain.util.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetAccountUseCase @Inject constructor(
    private val repository: AccountsRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(): Result<Account> {
        val activeAccountId = sessionRepository.getActiveAccountId().first()
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