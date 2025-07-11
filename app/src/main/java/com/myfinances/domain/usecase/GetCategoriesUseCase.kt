package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Category
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.util.Result
import javax.inject.Inject

/**
 * Use-case для получения полного списка всех доступных категорий транзакций.
 * Является простой оберткой над соответствующим методом репозитория.
 */

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoriesRepository
) {
    suspend operator fun invoke(): Result<List<Category>> {
        return repository.getCategories()
    }
}