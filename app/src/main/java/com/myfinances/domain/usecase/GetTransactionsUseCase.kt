package com.myfinances.domain.usecase

import com.myfinances.domain.entity.TransactionData
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.repository.TransactionsRepository
import com.myfinances.domain.util.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.Date
import javax.inject.Inject

/**
 * Use-case, который инкапсулирует бизнес-логику получения и обработки транзакций.
 * 1. Получает ID активного счета.
 * 2. Параллельно запрашивает детали счета, транзакции и категории.
 * 3. Фильтрует транзакции по типу (доход/расход).
 * 4. Сортирует транзакции по дате (новые вверху).
 * 5. Считает итоговую сумму.
 * 6. Возвращает агрегированные доменные данные в контейнере [TransactionData].
 */
class GetTransactionsUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val accountsRepository: AccountsRepository,
    private val getActiveAccountIdUseCase: GetActiveAccountIdUseCase
) {
    suspend operator fun invoke(
        startDate: Date,
        endDate: Date,
        filter: TransactionTypeFilter = TransactionTypeFilter.ALL
    ): Result<TransactionData> = coroutineScope {
        val accountIdResult = getActiveAccountIdUseCase()
        if (accountIdResult !is Result.Success) {
            return@coroutineScope handleError(accountIdResult)
        }
        val accountId = accountIdResult.data

        val accountDeferred = async { accountsRepository.getAccounts() }
        val transactionsDeferred =
            async { transactionsRepository.getTransactions(accountId, startDate, endDate) }
        val categoriesDeferred = async { categoriesRepository.getCategories() }

        val accountResult = accountDeferred.await()
        val transactionsResult = transactionsDeferred.await()
        val categoriesResult = categoriesDeferred.await()

        if (accountResult !is Result.Success) return@coroutineScope handleError(accountResult)
        if (transactionsResult !is Result.Success) return@coroutineScope handleError(
            transactionsResult
        )
        if (categoriesResult !is Result.Success) return@coroutineScope handleError(categoriesResult)

        val account = accountResult.data.find { it.id == accountId }
            ?: return@coroutineScope Result.Error(Exception("Активный счет не найден"))
        val transactions = transactionsResult.data
        val categories = categoriesResult.data
        val categoryMap = categories.associateBy { it.id }

        val filteredTransactions = when (filter) {
            TransactionTypeFilter.ALL -> transactions
            TransactionTypeFilter.INCOME -> transactions.filter {
                categoryMap[it.categoryId]?.isIncome == true
            }

            TransactionTypeFilter.EXPENSE -> transactions.filter {
                categoryMap[it.categoryId]?.isIncome == false
            }
        }

        val totalAmount = filteredTransactions.sumOf { it.amount }

        Result.Success(
            TransactionData(
                // ИЗМЕНЕНИЕ: Сортируем по дате в убывающем порядке (новые вверху)
                transactions = filteredTransactions.sortedByDescending { it.date },
                categories = categoryMap,
                account = account,
                totalAmount = totalAmount,
                startDate = startDate,
                endDate = endDate
            )
        )
    }

    private fun <T> handleError(result: Result<*>): Result<T> {
        return when (result) {
            is Result.Error -> Result.Error(result.exception)
            is Result.NetworkError -> Result.NetworkError
            else -> Result.Error(IllegalStateException("Неожиданный тип результата"))
        }
    }
}