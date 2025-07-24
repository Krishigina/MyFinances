package com.myfinances.domain.usecase

import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.domain.repository.TransactionsRepository
import com.myfinances.domain.util.Result
import javax.inject.Inject

/**
 * Use-case для удаления транзакции по её ID.
 */
class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionsRepository,
    private val accountUpdateManager: AccountUpdateManager
) {
    suspend operator fun invoke(transactionId: Int): Result<Unit> {
        val result = repository.deleteTransaction(transactionId)
        if (result is Result.Success) {
            accountUpdateManager.notifyAccountUpdated()
            repository.scheduleSync()
        }
        return result
    }
}