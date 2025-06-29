package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Account
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.util.Result
import javax.inject.Inject

/**
 * Use-case для получения основного (единственного) счета пользователя.
 * Инкапсулирует бизнес-логику по извлечению первого счета из списка,
 * полученного от репозитория.
 */
class GetAccountUseCase @Inject constructor(
    private val repository: AccountsRepository
) {
    suspend operator fun invoke(): Result<Account> {
        return when (val result = repository.getAccounts()) {
            is Result.Success -> {
                val firstAccount = result.data.firstOrNull()
                if (firstAccount != null) {
                    Result.Success(firstAccount)
                } else {
                    Result.Error(Exception("Счета для пользователя не найдены"))
                }
            }
            is Result.Error -> result
            is Result.NetworkError -> result
        }
    }
}