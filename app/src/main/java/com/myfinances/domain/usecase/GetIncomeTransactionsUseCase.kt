package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.repository.MyFinancesRepository
import com.myfinances.domain.util.Result
import java.util.Calendar
import javax.inject.Inject

class GetIncomeTransactionsUseCase @Inject constructor(
    private val repository: MyFinancesRepository
) {
    suspend operator fun invoke(accountId: Int): Result<Pair<List<Transaction>, List<Category>>> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = calendar.time

        val transactionsResult = repository.getTransactions(accountId, startDate, endDate)
        if (transactionsResult !is Result.Success) {
            @Suppress("UNCHECKED_CAST")
            return transactionsResult as Result<Nothing>
        }

        val categoriesResult = repository.getCategories()
        if (categoriesResult !is Result.Success) {
            @Suppress("UNCHECKED_CAST")
            return categoriesResult as Result<Nothing>
        }

        val allTransactions = transactionsResult.data
        val categories = categoriesResult.data
        val categoryMap = categories.associateBy { it.id }

        val incomeTransactions = allTransactions
            .filter { categoryMap[it.categoryId]?.isIncome == true }
            .sortedByDescending { it.date }

        return Result.Success(Pair(incomeTransactions, categories))
    }
}