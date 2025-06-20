package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.repository.MyFinancesRepository
import com.myfinances.domain.util.Result
import java.util.Calendar
import javax.inject.Inject

class GetExpenseTransactionsUseCase @Inject constructor(
    private val repository: MyFinancesRepository
) {
    suspend operator fun invoke(accountId: Int): Result<Pair<List<Transaction>, List<com.myfinances.domain.entity.Category>>> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = calendar.time

        val transactionsResult = repository.getTransactions(accountId, startDate, endDate)
        val categoriesResult = repository.getCategories()

        return when {
            transactionsResult is Result.Error -> transactionsResult
            categoriesResult is Result.Error -> categoriesResult
            transactionsResult is Result.Success && categoriesResult is Result.Success -> {
                val allTransactions = transactionsResult.data
                val categories = categoriesResult.data
                val categoryMap = categories.associateBy { it.id }

                val expenseTransactions = allTransactions
                    .filter { categoryMap[it.categoryId]?.isIncome == false }
                    .sortedByDescending { it.date }

                Result.Success(Pair(expenseTransactions, categories))
            }

            else -> Result.Error(IllegalStateException("Unknown state"))
        }
    }
}