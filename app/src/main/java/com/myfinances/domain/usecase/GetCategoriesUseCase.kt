package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Category
import com.myfinances.domain.repository.MyFinancesRepository
import com.myfinances.domain.util.Result

class GetCategoriesUseCase(
    private val repository: MyFinancesRepository
) {
    suspend operator fun invoke(): Result<List<Category>> {
        return repository.getCategories()
    }
}