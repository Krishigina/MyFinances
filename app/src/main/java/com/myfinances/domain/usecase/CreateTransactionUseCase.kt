package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.repository.TransactionsRepository
import com.myfinances.domain.util.Result
import java.util.Date
import javax.inject.Inject

/**
 * Use-case для создания новой транзакции.
 * Выполняет валидацию входных данных перед отправкой в репозиторий.
 */
class CreateTransactionUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val getActiveAccountIdUseCase: GetActiveAccountIdUseCase
) {
    suspend operator fun invoke(
        categoryId: Int,
        amount: String,
        transactionDate: Date,
        comment: String?
    ): Result<Transaction> {
        val accountIdResult = getActiveAccountIdUseCase()
        if (accountIdResult !is Result.Success) {
            return Result.Error(Exception("Не удалось определить активный счет"))
        }

        val amountAsDouble = amount.replace(',', '.').toDoubleOrNull()
        if (amountAsDouble == null || amountAsDouble <= 0) {
            return Result.Error(IllegalArgumentException("Сумма должна быть положительным числом"))
        }

        return transactionsRepository.createTransaction(
            accountId = accountIdResult.data,
            categoryId = categoryId,
            amount = amountAsDouble,
            transactionDate = transactionDate,
            comment = comment
        )
    }
}