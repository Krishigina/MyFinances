package com.myfinances.data.repository

import com.myfinances.data.network.ApiService
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.data.network.dto.toDomainModel
import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.repository.TransactionsRepository
import com.myfinances.domain.util.Result
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class TransactionsRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    connectivityManager: ConnectivityManagerSource
) : BaseRepository(connectivityManager), TransactionsRepository {

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
}