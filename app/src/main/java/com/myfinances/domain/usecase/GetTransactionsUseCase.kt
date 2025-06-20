package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.repository.MyFinancesRepository
import com.myfinances.domain.util.Result
import java.util.Date

class GetTransactionsUseCase(
    private val repository: MyFinancesRepository
) {
    suspend operator fun invoke(
        accountId: Int,
        startDate: Date,
        endDate: Date,
        filter: TransactionTypeFilter
    ): Result<Pair<List<Transaction>, List<Category>>> {

        val transactionsResult = repository.getTransactions(accountId, startDate, endDate)
        val categoriesResult = repository.getCategories()

        return when {
            transactionsResult is Result.Error -> transactionsResult
            categoriesResult is Result.Error -> categoriesResult
            transactionsResult is Result.Success && categoriesResult is Result.Success -> {
                val allTransactions = transactionsResult.data
                val categories = categoriesResult.data
                val categoryMap = categories.associateBy { it.id }

                val filteredTransactions = when (filter) {
                    TransactionTypeFilter.INCOME -> allTransactions.filter {
                        categoryMap[it.categoryId]?.isIncome == true
                    }

                    TransactionTypeFilter.EXPENSE -> allTransactions.filter {
                        categoryMap[it.categoryId]?.isIncome == false
                    }

                    TransactionTypeFilter.ALL -> allTransactions
                }

                Result.Success(Pair(filteredTransactions, categories))
            }

            else -> Result.Error(IllegalStateException("Unknown state during transaction fetching"))
        }
    }
}