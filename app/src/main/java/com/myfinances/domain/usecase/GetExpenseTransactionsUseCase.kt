package com.myfinances.domain.usecase

import com.myfinances.domain.entity.TransactionData
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.util.Result
import com.myfinances.domain.util.withTimeAtEndOfDay
import com.myfinances.domain.util.withTimeAtStartOfDay
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject

class GetExpenseTransactionsUseCase @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getActiveAccountIdUseCase: GetActiveAccountIdUseCase
) {
    private val calendar = Calendar.getInstance()
    private val endDate = calendar.withTimeAtEndOfDay().time
    private val startDate = calendar.withTimeAtStartOfDay().time

    operator fun invoke(): Flow<Result<TransactionData>> {
        return getTransactionsUseCase(
            startDate = startDate,
            endDate = endDate,
            filter = TransactionTypeFilter.EXPENSE
        )
    }

    suspend fun refresh(): Result<Unit> {
        return getTransactionsUseCase.refresh(startDate, endDate)
    }
}