package com.myfinances.domain.repository

import com.myfinances.domain.entity.Category
import com.myfinances.domain.util.Result

/**
 * Репозиторий для управления данными о категориях транзакций.
 */
interface CategoriesRepository {
    suspend fun getCategories(): Result<List<Category>>
}