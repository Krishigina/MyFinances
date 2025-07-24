package com.myfinances.data.repository

import android.content.Context
import android.util.Log
import com.myfinances.data.db.dao.AccountDao
import com.myfinances.data.db.dao.CategoryDao
import com.myfinances.data.db.dao.TransactionDao
import com.myfinances.data.db.entity.AccountEntity
import com.myfinances.data.db.entity.toEntity
import com.myfinances.data.network.ApiService
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.data.network.CreateTransactionRequest
import com.myfinances.data.network.dto.UpdateAccountRequest
import com.myfinances.data.network.dto.UpdateTransactionRequest
import com.myfinances.data.network.dto.toDomainModel
import com.myfinances.domain.repository.SessionRepository
import com.myfinances.domain.repository.SyncRepository
import com.myfinances.domain.util.Result
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val connectivityManager: ConnectivityManagerSource,
    private val context: Context,
    private val sessionRepository: SessionRepository
) : SyncRepository {

    private val transactionsRepository: TransactionsRepositoryImpl by lazy {
        TransactionsRepositoryImpl(
            context = context,
            apiService = apiService,
            transactionDao = transactionDao,
            connectivityManager = connectivityManager
        )
    }

    override suspend fun syncData(): Result<Unit> {
        if (!connectivityManager.isNetworkAvailable.first()) {
            return Result.Failure.NetworkError
        }

        return try {
            coroutineScope {
                Log.d("SyncWorker", "Starting data synchronization...")
                launch { syncLocalChangesToServer() }.join()
                launch { refreshAllDataFromServer() }.join()
            }
            sessionRepository.setLastSyncTime(System.currentTimeMillis())
            Log.d("SyncWorker", "Synchronization finished successfully.")
            Result.Success(Unit)
        } catch (e: IOException) {
            Log.e("SyncWorker", "Network error during sync", e)
            Result.Failure.NetworkError
        } catch (e: Exception) {
            Log.e("SyncWorker", "Generic error during sync", e)
            Result.Failure.GenericError(e)
        }
    }

    private suspend fun syncLocalChangesToServer() {
        val unsyncedTransactions = transactionDao.getUnsyncedTransactions()
        if (unsyncedTransactions.isEmpty()) {
            Log.d("SyncWorker", "No local changes to sync.")
            return
        }

        val unsyncedAccounts = accountDao.getUnsyncedAccounts()
        if (unsyncedAccounts.isNotEmpty()) {
            Log.d("SyncWorker", "Found ${unsyncedAccounts.size} unsynced accounts.")
            for (accountEntity in unsyncedAccounts) {
                try {
                    val serverResponse = apiService.getAccountById(accountEntity.id)
                    if (!serverResponse.isSuccessful || serverResponse.body() == null) {
                        Log.e("SyncWorker", "Failed to fetch account ${accountEntity.id} for conflict check.")
                        continue
                    }

                    val serverAccountDto = serverResponse.body()!!
                    val serverUpdatedAt = parseTimestamp(serverAccountDto.updatedAt) ?: 0L

                    if (serverUpdatedAt > accountEntity.lastUpdatedAt) {
                        Log.w("SyncWorker", "Conflict for account ${accountEntity.id}. Server is newer. Discarding local changes.")
                        accountDao.upsertAll(listOf(serverAccountDto.toDomainModel().toEntity(isSynced = true)))
                    } else {
                        val request = UpdateAccountRequest(
                            name = accountEntity.name,
                            balance = accountEntity.balance.toString(),
                            currency = accountEntity.currency
                        )
                        val updateResponse = apiService.updateAccount(accountEntity.id, request)
                        if (updateResponse.isSuccessful && updateResponse.body() != null) {
                            val updatedDto = updateResponse.body()!!
                            accountDao.upsertAll(listOf(updatedDto.toDomainModel().toEntity(isSynced = true)))
                            Log.d("SyncWorker", "Synced account ${accountEntity.id} to server.")
                        } else {
                            Log.e("SyncWorker", "Failed to sync account ${accountEntity.id}. Code: ${updateResponse.code()}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SyncWorker", "Error syncing account ${accountEntity.id}", e)
                }
            }
        }

        if (unsyncedTransactions.isEmpty() && unsyncedAccounts.isEmpty()) {
            Log.d("SyncWorker", "No local changes to sync.")
        }
    }

    private suspend fun refreshAllDataFromServer() {
        Log.d("SyncWorker", "Refreshing all data from server...")
        try {
            val accountsResponse = apiService.getAccounts()
            if (accountsResponse.isSuccessful) {
                accountsResponse.body()?.let { dtos ->
                    val localAccountsMap = accountDao.getAccounts().first().associateBy { it.id }
                    val accountsToUpsert = mutableListOf<AccountEntity>()

                    for (dto in dtos) {
                        val serverDomainModel = dto.toDomainModel()
                        val serverEntity = serverDomainModel.toEntity(isSynced = true)
                        val localEntity = localAccountsMap[serverEntity.id]

                        if (localEntity == null) {
                            accountsToUpsert.add(serverEntity)
                        } else {
                            if (serverEntity.lastUpdatedAt > localEntity.lastUpdatedAt) {
                                accountsToUpsert.add(serverEntity)
                            }
                        }
                    }

                    if (accountsToUpsert.isNotEmpty()) {
                        Log.d("SyncWorker", "Upserting ${accountsToUpsert.size} accounts from server.")
                        accountDao.upsertAll(accountsToUpsert)
                    }
                }
            }

            val categoriesResponse = apiService.getCategories()
            if (categoriesResponse.isSuccessful) {
                categoriesResponse.body()?.let { dtos ->
                    val entities = dtos.map { it.toDomainModel().toEntity() }
                    categoryDao.upsertAll(entities)
                }
            }

            val calendar = Calendar.getInstance()
            val endDate = calendar.time
            calendar.add(Calendar.MONTH, -3)
            val startDate = calendar.time

            val accounts = accountDao.getAccounts().first()
            for (account in accounts) {
                transactionsRepository.refreshTransactions(account.id, startDate, endDate)
            }
            Log.d("SyncWorker", "Refreshed accounts, categories, and recent transactions.")
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error refreshing data from server", e)
        }
    }
}

private fun parseTimestamp(dateString: String): Long? {
    return try {
        Instant.parse(dateString).toEpochMilli()
    } catch (e: DateTimeParseException) {
        null
    }
}