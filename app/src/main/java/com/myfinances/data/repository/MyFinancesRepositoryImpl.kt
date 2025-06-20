package com.myfinances.data.repository

import com.myfinances.data.network.ApiService
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.data.network.dto.toDomainModel
import com.myfinances.domain.entity.Account
import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.repository.MyFinancesRepository
import com.myfinances.domain.util.Result
import kotlinx.coroutines.flow.first
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class MyFinancesRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val connectivityManager: ConnectivityManagerSource
) : MyFinancesRepository {

    private suspend fun <T : Any> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
        if (!connectivityManager.isNetworkAvailable.first()) {
            return Result.NetworkError
        }
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("API Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: IOException) {
            Result.NetworkError
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getTransactions(
        accountId: Int,
        startDate: Date,
        endDate: Date
    ): Result<List<Transaction>> {
        return when (
            val result = safeApiCall {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                apiService.getTransactionsForPeriod(
                    accountId = accountId,
                    startDate = dateFormat.format(startDate),
                    endDate = dateFormat.format(endDate)
                )
            }
        ) {
            is Result.Success -> Result.Success(result.data.mapNotNull { it.toDomainModel() })
            is Result.Error -> result
            is Result.NetworkError -> result
        }
    }

    override suspend fun getCategories(): Result<List<Category>> {
        return when (val result = safeApiCall { apiService.getCategories() }) {
            is Result.Success -> Result.Success(result.data.map { it.toDomainModel() })
            is Result.Error -> result
            is Result.NetworkError -> result
        }
    }

    override suspend fun getAccounts(): Result<List<Account>> {
        TODO("Not yet implemented")
    }
}