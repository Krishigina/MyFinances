package com.myfinances.domain.usecase

import com.myfinances.domain.entity.TransactionData
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.repository.TransactionsRepository
import com.myfinances.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import java.util.Date

class GetTransactionsUseCase(
    private val transactionsRepository: TransactionsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val accountsRepository: AccountsRepository,
    private val getActiveAccountIdUseCase: GetActiveAccountIdUseCase
) {
    operator fun invoke(
        startDate: Date,
        endDate: Date,
        filter: TransactionTypeFilter = TransactionTypeFilter.ALL
    ): Flow<Result<TransactionData>> {
        // Создаем поток, который один раз эмитит ID активного счета или выбрасывает ошибку
        val activeAccountIdFlow = flow {
            when (val result = getActiveAccountIdUseCase()) {
                is Result.Success -> emit(result.data)
                is Result.Error -> throw result.exception
                is Result.NetworkError -> throw IllegalStateException("Network error during account ID fetch")
            }
        }

        // Используем flatMapLatest, чтобы при получении accountId создать новый комбинированный поток
        return activeAccountIdFlow.flatMapLatest { accountId ->
            val transactionsFlow = transactionsRepository.getTransactions(accountId, startDate, endDate)
            val categoriesFlow = categoriesRepository.getCategories()
            val accountsFlow = accountsRepository.getAccounts()

            // Теперь комбинируем все нужные потоки
            combine(transactionsFlow, categoriesFlow, accountsFlow) { transactions, categories, accounts ->
                val account = accounts.find { it.id == accountId }
                    ?: return@combine Result.Error(Exception("Активный счет не найден"))

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
                        transactions = filteredTransactions.sortedByDescending { it.date },
                        categories = categoryMap,
                        account = account,
                        totalAmount = totalAmount,
                        startDate = startDate,
                        endDate = endDate
                    )
                )
            }
        }.catch { e ->
            // Отлавливаем исключения из activeAccountIdFlow
            emit(Result.Error(e))
        }
    }

    suspend fun refresh(startDate: Date, endDate: Date): Result<Unit> {
        // Получаем ID счета для обновления
        val accountIdResult = getActiveAccountIdUseCase()
        if (accountIdResult !is Result.Success) {
            return Result.Error(Exception("Active account could not be determined for refresh."))
        }
        val accountId = accountIdResult.data

        val transactionResult = transactionsRepository.refreshTransactions(accountId, startDate, endDate)
        val categoryResult = categoriesRepository.refreshCategories()
        val accountResult = accountsRepository.refreshAccounts()

        // Проверяем результаты на ошибки
        val errors = listOf(transactionResult, categoryResult, accountResult).filterIsInstance<Result.Error>()
        if (errors.isNotEmpty()) {
            return Result.Error(errors.first().exception)
        }
        val networkError = listOf(transactionResult, categoryResult, accountResult).filterIsInstance<Result.NetworkError>()
        if (networkError.isNotEmpty()) {
            return Result.NetworkError
        }

        return Result.Success(Unit)
    }
}