package com.myfinances.data.repository

import com.myfinances.data.db.dao.CategoryDao
import com.myfinances.data.db.entity.toEntity
import com.myfinances.data.network.ApiService
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.data.network.dto.toDomainModel
import com.myfinances.domain.entity.Category
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val categoryDao: CategoryDao,
    private val connectivityManager: ConnectivityManagerSource
) : CategoriesRepository {

    override fun getCategories(): Flow<List<Category>> {
        // Возвращаем Flow из DAO, который будет автоматически обновлять UI
        // при изменениях в таблице 'categories'.
        return categoryDao.getCategories().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun refreshCategories(): Result<Unit> {
        // Проверяем, есть ли интернет
        if (!connectivityManager.isNetworkAvailable.first()) {
            return Result.NetworkError
        }

        return try {
            // Выполняем сетевой запрос
            val response = apiService.getCategories()

            if (response.isSuccessful) {
                val dtos = response.body()
                if (dtos != null) {
                    // Если данные получены успешно, преобразуем их в Entity
                    // и сохраняем в базу данных.
                    val entities = dtos.map { it.toDomainModel().toEntity() }
                    categoryDao.upsertAll(entities)
                    Result.Success(Unit)
                } else {
                    Result.Error(Exception("Empty response body"))
                }
            } else {
                Result.Error(Exception("API Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: IOException) {
            Result.NetworkError
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}