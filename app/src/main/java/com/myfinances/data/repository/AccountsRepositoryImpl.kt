package com.myfinances.data.repository

import com.myfinances.data.network.ApiService
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.data.network.dto.UpdateAccountRequest
import com.myfinances.data.network.dto.toDomainModel
import com.myfinances.domain.entity.Account
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.util.Result
import javax.inject.Inject

/**
 * Реализация [AccountsRepository], которая получает данные о счетах пользователя
 * из удаленного источника данных (API).
 */
class AccountsRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    connectivityManager: ConnectivityManagerSource
) : BaseRepository(connectivityManager), AccountsRepository {

    override suspend fun getAccounts(): Result<List<Account>> {
        return when (val result = safeApiCall { apiService.getAccounts() }) {
            is Result.Success -> Result.Success(result.data.map { it.toDomainModel() })
            is Result.Error -> result
            is Result.NetworkError -> result
        }
    }

    override suspend fun updateAccount(
        accountId: Int,
        name: String,
        balance: Double,
        currency: String
    ): Result<Account> {
        val request = UpdateAccountRequest(
            name = name,
            balance = balance.toString(),
            currency = currency
        )
        return when (val result = safeApiCall { apiService.updateAccount(accountId, request) }) {
            is Result.Success -> Result.Success(result.data.toDomainModel())
            is Result.Error -> result
            is Result.NetworkError -> result
        }
    }
}