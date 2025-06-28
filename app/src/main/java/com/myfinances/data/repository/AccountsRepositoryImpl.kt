package com.myfinances.data.repository

import android.util.Log
import com.myfinances.data.network.ApiService
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.data.network.dto.toDomainModel
import com.myfinances.domain.entity.Account
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.util.Result
import javax.inject.Inject

class AccountsRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    connectivityManager: ConnectivityManagerSource
) : BaseRepository(connectivityManager), AccountsRepository {

    override suspend fun getAccounts(): Result<List<Account>> {
        return when (val result = safeApiCall { apiService.getAccounts() }) {
            is Result.Success -> {
                Log.d("AccountBalanceDebug", "Raw DTOs from server: ${result.data}")
                val domainAccounts = result.data.map { it.toDomainModel() }
                Log.d("AccountBalanceDebug", "Domain models after mapping: $domainAccounts")
                Result.Success(domainAccounts)
            }

            is Result.Error -> result
            is Result.NetworkError -> result
        }
    }
}