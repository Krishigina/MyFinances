package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCategoriesUseCase(
    private val repository: CategoriesRepository
) {
    operator fun invoke(filter: TransactionTypeFilter = TransactionTypeFilter.EXPENSE): Flow<List<Category>> {
        return repository.getCategories().map { categories ->
            when (filter) {
                TransactionTypeFilter.INCOME -> categories.filter { it.isIncome }
                TransactionTypeFilter.EXPENSE -> categories.filter { !it.isIncome }
                TransactionTypeFilter.ALL -> categories
            }.sortedBy { it.name }
        }
    }

    suspend fun refresh(): Result<Unit> {
        return repository.refreshCategories()
    }
}