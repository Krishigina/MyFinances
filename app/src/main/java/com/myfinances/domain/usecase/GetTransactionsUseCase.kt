package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.repository.TransactionsRepository
import com.myfinances.domain.util.Result
import java.util.Date
import javax.inject.Inject

/**
 * Универсальный use-case для получения списка транзакций.
 * Инкапсулирует логику:
 * 1. Получение транзакций за указанный период.
 * 2. Получение всех категорий.
 * 3. Фильтрация транзакций по типу (доходы, расходы, все).
 * 4. Возврат пары из отфильтрованного списка транзакций и полного списка категорий,
 *    чтобы UI мог легко сопоставить транзакцию с ее категорией.
 */
class GetTransactionsUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val categoriesRepository: CategoriesRepository
) {
    suspend operator fun invoke(
        accountId: Int,
        startDate: Date,
        endDate: Date,
        filter: TransactionTypeFilter
    ): Result<Pair<List<Transaction>, List<Category>>> {

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