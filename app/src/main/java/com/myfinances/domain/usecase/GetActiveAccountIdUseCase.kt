package com.myfinances.domain.usecase

import com.myfinances.data.store.SessionStore
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.util.Result
import javax.inject.Inject

/**
 * Use-case для получения ID активного счета.
 * Инкапсулирует сложную логику определения ID, скрывая ее от ViewModel.
 * Логика работы:
 * 1. Проверить кэш в памяти ([SessionStore]).
 * 2. Если ID найден - вернуть его.
 * 3. Если не найден - запросить счета из сети, взять ID первого,
 *    сохранить его в кэш для текущей сессии и вернуть.
 */
class GetActiveAccountIdUseCase @Inject constructor(
    private val repository: AccountsRepository,
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
                    Result.Error(Exception("У пользователя нет счетов"))
                }
            }
            is Result.Error -> accountsResult
            is Result.NetworkError -> accountsResult
        }
    }
}