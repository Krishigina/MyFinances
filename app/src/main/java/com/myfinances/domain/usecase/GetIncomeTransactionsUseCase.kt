package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.util.Result
import com.myfinances.domain.util.withTimeAtEndOfDay
import com.myfinances.domain.util.withTimeAtStartOfDay
import java.util.Calendar
import javax.inject.Inject

/**
 * Use-case для получения списка транзакций доходов **только за сегодня**.
 * Инкапсулирует бизнес-правило "сегодняшний день" и использует более общий
 * [GetTransactionsUseCase] для выполнения основной работы.
 */
class GetIncomeTransactionsUseCase @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase
) {
    suspend operator fun invoke(accountId: Int): Result<Pair<List<Transaction>, List<Category>>> {
        val calendar = Calendar.getInstance()

        val endDate = calendar.withTimeAtEndOfDay().time
        val startDate = calendar.withTimeAtStartOfDay().time

        return getTransactionsUseCase(
            accountId = accountId,
            startDate = startDate,
            endDate = endDate,
            filter = TransactionTypeFilter.INCOME
        )
    }
}