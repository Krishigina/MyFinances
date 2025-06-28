package com.myfinances.data.repository

import com.myfinances.data.network.ApiService
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.data.network.dto.toDomainModel
import com.myfinances.domain.entity.Category
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.util.Result
import javax.inject.Inject

/**
 * Реализация интерфейса [CategoriesRepository] из доменного слоя.
 * Класс отвечает за получение списка категорий транзакций из удаленного API.
 */
class CategoriesRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    connectivityManager: ConnectivityManagerSource
) : BaseRepository(connectivityManager), CategoriesRepository {

    override suspend fun getCategories(): Result<List<Category>> {
        return when (val result = safeApiCall { apiService.getCategories() }) {
            is Result.Success -> Result.Success(result.data.map { it.toDomainModel() })
            is Result.Error -> result
            is Result.NetworkError -> result
        }
    }
}