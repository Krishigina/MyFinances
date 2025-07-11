package com.myfinances.domain.usecase

import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.CategoryDomainToUiMapper
import com.myfinances.ui.model.ArticlesUiModel
import javax.inject.Inject

/**
 * Use-case для получения и подготовки списка статей расходов для UI.
 * 1. Запрашивает все категории.
 * 2. Фильтрует, оставляя только статьи расходов.
 * 3. Сортирует по имени.
 * 4. Маппит в готовую для UI модель.
 */
class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoriesRepository,
    private val mapper: CategoryDomainToUiMapper
) {
    suspend operator fun invoke(): Result<ArticlesUiModel> {
        return when (val result = repository.getCategories()) {
            is Result.Success -> {
                val expenseCategories = result.data
                    .filter { !it.isIncome }
                    .sortedBy { it.name }
                    .map { mapper.toListItemModel(it) }

                Result.Success(ArticlesUiModel(expenseCategories))
            }

            is Result.Error -> result
            is Result.NetworkError -> result
        }
    }
}