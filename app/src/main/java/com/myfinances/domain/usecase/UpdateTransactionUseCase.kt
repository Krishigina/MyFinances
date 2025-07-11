package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.repository.TransactionsRepository
import com.myfinances.domain.util.Result
import java.util.Date
import javax.inject.Inject

/**
 * Use-case для обновления существующей транзакции.
 * Выполняет валидацию входных данных перед отправкой в репозиторий.
 */
class UpdateTransactionUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository
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
            return Result.Error(IllegalArgumentException("Сумма должна быть положительным числом"))
        }

        return transactionsRepository.updateTransaction(
            transactionId = transactionId,
            accountId = accountId,
            categoryId = categoryId,
            amount = amountAsDouble,
            transactionDate = transactionDate,
            comment = comment
        )
    }
}