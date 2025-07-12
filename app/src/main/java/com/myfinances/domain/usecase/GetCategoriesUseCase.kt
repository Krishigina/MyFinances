package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.util.Result

class GetCategoriesUseCase(
    private val repository: CategoriesRepository
) {
    suspend operator fun invoke(filter: TransactionTypeFilter = TransactionTypeFilter.EXPENSE): Result<List<Category>> {
        return when (val result = repository.getCategories()) {
            is Result.Success -> {
                val filteredCategories = when (filter) {
                    TransactionTypeFilter.INCOME -> result.data.filter { it.isIncome }
                    TransactionTypeFilter.EXPENSE -> result.data.filter { !it.isIncome }
                    TransactionTypeFilter.ALL -> result.data
                }
                Result.Success(filteredCategories.sortedBy { it.name })
            }
            is Result.Error -> result
            is Result.NetworkError -> result
        }
    }
}