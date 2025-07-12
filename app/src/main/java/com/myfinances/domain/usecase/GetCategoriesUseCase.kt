package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Category
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.util.Result

class GetCategoriesUseCase(
    private val repository: CategoriesRepository
) {
    suspend operator fun invoke(): Result<List<Category>> {
        return when (val result = repository.getCategories()) {
            is Result.Success -> {
                val expenseCategories = result.data
                    .filter { !it.isIncome }
                    .sortedBy { it.name }
                Result.Success(expenseCategories)
            }
            is Result.Error -> result
            is Result.NetworkError -> result
        }
    }
}