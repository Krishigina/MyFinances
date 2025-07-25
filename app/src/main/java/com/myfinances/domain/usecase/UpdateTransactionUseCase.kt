package com.myfinances.domain.usecase

import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.repository.TransactionsRepository
import com.myfinances.domain.util.Result
import java.util.Date
import javax.inject.Inject

class UpdateTransactionUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val accountUpdateManager: AccountUpdateManager
) {
    suspend operator fun invoke(
        transactionId: Int,
        accountId: Int,
        categoryId: Int,
        amount: String,
        transactionDate: Date,
        comment: String
    ): Result<Transaction> {
        val amountAsDouble = amount.replace(',', '.').toDoubleOrNull()
        if (amountAsDouble == null || amountAsDouble <= 0) {
            return Result.Failure.GenericError(IllegalArgumentException("Сумма должна быть положительным числом"))
        }

        val result = transactionsRepository.updateTransaction(
            transactionId = transactionId,
            accountId = accountId,
            categoryId = categoryId,
            amount = amountAsDouble,
            transactionDate = transactionDate,
            comment = comment
        )

        if (result is Result.Success) {
            accountUpdateManager.notifyAccountUpdated()
            transactionsRepository.scheduleSync()
        }

        return result
    }
}