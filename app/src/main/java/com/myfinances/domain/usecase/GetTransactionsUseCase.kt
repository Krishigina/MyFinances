package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.repository.MyFinancesRepository
import com.myfinances.domain.util.Result
import java.util.Date
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: MyFinancesRepository
) {
    suspend operator fun invoke(
        accountId: Int,
        startDate: Date,
        endDate: Date,
        filter: TransactionTypeFilter
    ): Result<Pair<List<Transaction>, List<Category>>> {

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

        val filteredTransactions = when (filter) {
            TransactionTypeFilter.INCOME -> allTransactions.filter {
                categoryMap[it.categoryId]?.isIncome == true
            }

            TransactionTypeFilter.EXPENSE -> allTransactions.filter {
                categoryMap[it.categoryId]?.isIncome == false
            }

            TransactionTypeFilter.ALL -> allTransactions
        }

        return Result.Success(Pair(filteredTransactions, categories))
    }
}