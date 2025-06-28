package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.repository.TransactionsRepository
import com.myfinances.domain.util.Result
import java.util.Calendar
import javax.inject.Inject

class GetExpenseTransactionsUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val categoriesRepository: CategoriesRepository
) {
    suspend operator fun invoke(accountId: Int): Result<Pair<List<Transaction>, List<Category>>> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = calendar.time

        val transactionsResult =
            transactionsRepository.getTransactions(accountId, startDate, endDate)
        if (transactionsResult !is Result.Success) {
            @Suppress("UNCHECKED_CAST")
            return transactionsResult as Result<Nothing>
        }

        val categoriesResult = categoriesRepository.getCategories()
        if (categoriesResult !is Result.Success) {
            @Suppress("UNCHECKED_CAST")
            return categoriesResult as Result<Nothing>
        }

        val allTransactions = transactionsResult.data
        val categories = categoriesResult.data
        val categoryMap = categories.associateBy { it.id }

        val expenseTransactions = allTransactions
            .filter { categoryMap[it.categoryId]?.isIncome == false }
            .sortedByDescending { it.date }

        return Result.Success(Pair(expenseTransactions, categories))
    }
}