package com.myfinances.domain.usecase

import com.myfinances.domain.repository.TransactionsRepository
import com.myfinances.domain.util.Result
import javax.inject.Inject

/**
 * Use-case для удаления транзакции по её ID.
 */
class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionsRepository
) {
    suspend operator fun invoke(transactionId: Int): Result<Unit> {
        return repository.deleteTransaction(transactionId)
    }
}