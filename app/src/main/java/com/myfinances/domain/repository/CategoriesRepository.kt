package com.myfinances.domain.repository

import com.myfinances.domain.entity.Category
import com.myfinances.domain.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для управления данными о категориях транзакций.
 */
interface CategoriesRepository {
    fun getCategories(): Flow<List<Category>>

    suspend fun refreshCategories(): Result<Unit>
}