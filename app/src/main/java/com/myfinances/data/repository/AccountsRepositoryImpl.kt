package com.myfinances.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.myfinances.data.db.dao.AccountDao
import com.myfinances.data.db.entity.toEntity
import com.myfinances.data.network.ApiService
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.data.network.dto.UpdateAccountRequest
import com.myfinances.data.network.dto.toDomainModel
import com.myfinances.data.workers.SyncWorker
import com.myfinances.domain.entity.Account
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class AccountsRepositoryImpl @Inject constructor(
    private val context: Context,
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
            return Result.Failure.NetworkError
        }

        return try {
            val response = apiService.getAccounts()
            if (response.isSuccessful) {
                val dtos = response.body()
                if (dtos != null) {
                    val entities = dtos.map { it.toDomainModel().toEntity(isSynced = true) }
                    accountDao.upsertAll(entities)
                    Result.Success(Unit)
                } else {
                    Result.Failure.GenericError(Exception("Empty response body"))
                }
            } else {
                Result.Failure.ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            Result.Failure.NetworkError
        } catch (e: Exception) {
            Result.Failure.GenericError(e)
        }
    }

    override suspend fun updateAccount(
        accountId: Int,
        name: String,
        balance: Double,
        currency: String
    ): Result<Account> {

        try {
            val currentAccount = getAccounts().first().find { it.id == accountId }
            val updatedAccount = Account(
                id = accountId,
                name = name,
                balance = balance,
                currency = currency,
                emoji = currentAccount?.emoji ?: "💰",
                lastUpdatedAt = System.currentTimeMillis()
            )

            accountDao.upsertAll(listOf(updatedAccount.toEntity(isSynced = false)))

            return Result.Success(updatedAccount)
        } catch (e: Exception) {
            return Result.Failure.GenericError(e)
        }
    }

    override fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "one-time-sync",
            ExistingWorkPolicy.KEEP,
            syncRequest
        )
    }
}