package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.repository.TransactionsRepository
import com.myfinances.domain.util.Result
import javax.inject.Inject

/**
 * Use-case для получения детальной информации о транзакции по её ID.
 */
class GetTransactionDetailsUseCase @Inject constructor(
    private val repository: TransactionsRepository
) {
    suspend operator fun invoke(transactionId: Int): Result<Transaction> {
        return repository.getTransactionById(transactionId)
    }
}