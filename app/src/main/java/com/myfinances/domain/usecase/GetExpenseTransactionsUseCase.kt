package com.myfinances.domain.usecase

import com.myfinances.domain.entity.TransactionData
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.util.Result
import com.myfinances.domain.util.withTimeAtEndOfDay
import com.myfinances.domain.util.withTimeAtStartOfDay
import java.util.Calendar
import javax.inject.Inject

/**
 * Use-case для получения данных о расходах **только за сегодня**.
 * Инкапсулирует бизнес-правило "сегодняшний день" и использует более общий
 * [GetTransactionsUseCase] для выполнения основной работы.
 * Возвращает сырые, но отфильтрованные доменные данные.
 */
class GetExpenseTransactionsUseCase @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase
) {
    suspend operator fun invoke(): Result<TransactionData> {
        val calendar = Calendar.getInstance()

        val endDate = calendar.withTimeAtEndOfDay().time
        val startDate = calendar.withTimeAtStartOfDay().time

        return getTransactionsUseCase(
            startDate = startDate,
            endDate = endDate,
            filter = TransactionTypeFilter.EXPENSE
        )
    }
}