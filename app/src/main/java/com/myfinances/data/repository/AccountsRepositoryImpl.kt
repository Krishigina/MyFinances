package com.myfinances.data.repository

import com.myfinances.data.db.dao.AccountDao
import com.myfinances.data.db.entity.toEntity
import com.myfinances.data.network.ApiService
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.data.network.dto.UpdateAccountRequest
import com.myfinances.data.network.dto.toDomainModel
import com.myfinances.domain.entity.Account
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class AccountsRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val accountDao: AccountDao,
    private val connectivityManager: ConnectivityManagerSource
) : AccountsRepository {

    override fun getAccounts(): Flow<List<Account>> {
        return accountDao.getAccounts().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun refreshAccounts(): Result<Unit> {
        if (!connectivityManager.isNetworkAvailable.first()) {
            return Result.NetworkError
        }

        return try {
            val response = apiService.getAccounts()
            if (response.isSuccessful) {
                val dtos = response.body()
                if (dtos != null) {
                    val entities = dtos.map { it.toDomainModel().toEntity() }
                    accountDao.upsertAll(entities)
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

    override suspend fun updateAccount(
        accountId: Int,
        name: String,
        balance: Double,
        currency: String
    ): Result<Account> {
        if (!connectivityManager.isNetworkAvailable.first()) {
            // TODO: В будущем здесь нужно будет сохранять изменения локально
            // и синхронизировать их позже. Пока просто возвращаем ошибку.
            return Result.NetworkError
        }

        val request = UpdateAccountRequest(
            name = name,
            balance = balance.toString(),
            currency = currency
        )

        return try {
            val response = apiService.updateAccount(accountId, request)
            if (response.isSuccessful && response.body() != null) {
                val updatedAccount = response.body()!!.toDomainModel()
                accountDao.upsertAll(listOf(updatedAccount.toEntity()))
                Result.Success(updatedAccount)
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