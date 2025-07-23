package com.myfinances.domain.usecase

import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.domain.entity.Account
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.util.Result
import javax.inject.Inject

class UpdateAccountUseCase @Inject constructor(
    private val repository: AccountsRepository,
    private val accountUpdateManager: AccountUpdateManager
) {
    suspend operator fun invoke(
        accountId: Int,
        name: String,
        balance: String,
        currency: String
    ): Result<Account> {
        if (name.isBlank()) {
            return Result.Failure.GenericError(IllegalArgumentException("Название счета не может быть пустым"))
        }
        val balanceAsDouble = balance.replace(',', '.').toDoubleOrNull()
            ?: return Result.Failure.GenericError(IllegalArgumentException("Некорректный формат баланса"))

        val result = repository.updateAccount(accountId, name, balanceAsDouble, currency)

        if (result is Result.Success) {
            accountUpdateManager.notifyAccountUpdated()
        }

        return result
    }
}