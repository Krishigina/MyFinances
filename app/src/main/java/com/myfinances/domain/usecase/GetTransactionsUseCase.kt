package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.repository.MyFinancesRepository
import com.myfinances.domain.util.Result
import java.util.Date

class GetTransactionsUseCase(
    private val repository: MyFinancesRepository
) {
    suspend operator fun invoke(
        accountId: Int,
        startDate: Date,
        endDate: Date
    ): Result<List<Transaction>> {
        return repository.getTransactions(accountId, startDate, endDate)
    }
}