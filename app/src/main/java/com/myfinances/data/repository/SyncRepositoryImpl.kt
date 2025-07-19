package com.myfinances.data.repository

import android.content.Context
import android.util.Log
import com.myfinances.data.db.dao.AccountDao
import com.myfinances.data.db.dao.CategoryDao
import com.myfinances.data.db.dao.TransactionDao
import com.myfinances.data.db.entity.toEntity
import com.myfinances.data.network.ApiService
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.data.network.CreateTransactionRequest
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

    private val apiDateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

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
            return Result.NetworkError
        }

        return try {
            coroutineScope {
                Log.d("SyncWorker", "Starting data synchronization...")
                val localSyncJob = launch { syncLocalChangesToServer() }
                localSyncJob.join()
                val remoteRefreshJob = launch { refreshAllDataFromServer() }
                remoteRefreshJob.join()
            }
            sessionRepository.setLastSyncTime(System.currentTimeMillis())
            Log.d("SyncWorker", "Synchronization finished successfully.")
            Result.Success(Unit)
        } catch (e: IOException) {
            Log.e("SyncWorker", "Network error during sync", e)
            Result.NetworkError
        } catch (e: Exception) {
            Log.e("SyncWorker", "Generic error during sync", e)
            Result.Error(e)
        }
    }

    private suspend fun syncLocalChangesToServer() {
        val unsyncedTransactions = transactionDao.getUnsyncedTransactions()
        if (unsyncedTransactions.isEmpty()) {
            Log.d("SyncWorker", "No local changes to sync.")
            return
        }

        Log.d("SyncWorker", "Found ${unsyncedTransactions.size} unsynced transactions.")

        for (transactionEntity in unsyncedTransactions) {
            when {
                transactionEntity.isDeletedLocally -> {
                    val response = apiService.deleteTransaction(transactionEntity.id)
                    if (response.isSuccessful) {
                        transactionDao.deleteById(transactionEntity.id)
                        Log.d("SyncWorker", "Deleted transaction ${transactionEntity.id} from server and local DB.")
                    } else {
                        Log.e("SyncWorker", "Failed to delete transaction ${transactionEntity.id} on server. Code: ${response.code()}")
                    }
                }
                transactionEntity.id < 0 -> {
                    val request = CreateTransactionRequest(
                        accountId = transactionEntity.accountId,
                        categoryId = transactionEntity.categoryId ?: 0,
                        amount = transactionEntity.amount.toString(),
                        transactionDate = apiDateFormat.format(transactionEntity.date),
                        comment = transactionEntity.comment
                    )
                    val response = apiService.createTransaction(request)
                    if (response.isSuccessful && response.body() != null) {
                        val newTransaction = response.body()!!.toDomainModel()
                        if (newTransaction != null) {
                            transactionDao.deleteById(transactionEntity.id)
                            transactionDao.upsert(newTransaction.toEntity(isSynced = true))
                            Log.d("SyncWorker", "Created new transaction. Local ID ${transactionEntity.id} replaced with server ID ${newTransaction.id}.")
                        }
                    } else {
                        Log.e("SyncWorker", "Failed to create transaction on server. Code: ${response.code()}")
                    }
                }
                else -> { // Логика разрешения конфликтов для измененных транзакций
                    try {
                        val serverResponse = apiService.getTransactionById(transactionEntity.id)
                        if (!serverResponse.isSuccessful || serverResponse.body() == null) {
                            Log.e("SyncWorker", "Failed to fetch transaction ${transactionEntity.id} for conflict check.")
                            continue
                        }

                        val serverTransactionDto = serverResponse.body()!!
                        val serverUpdatedAt = serverTransactionDto.updatedAt?.let { parseTimestamp(it) } ?: 0L

                        if (serverUpdatedAt > transactionEntity.lastUpdatedAt) {
                            Log.w("SyncWorker", "Conflict detected for transaction ${transactionEntity.id}. Server is newer. Discarding local changes.")
                            serverTransactionDto.toDomainModel()?.let {
                                transactionDao.upsert(it.toEntity(isSynced = true))
                            }
                        } else {
                            val request = UpdateTransactionRequest(
                                accountId = transactionEntity.accountId,
                                categoryId = transactionEntity.categoryId ?: 0,
                                amount = transactionEntity.amount.toString(),
                                transactionDate = apiDateFormat.format(transactionEntity.date),
                                comment = transactionEntity.comment
                            )
                            val updateResponse = apiService.updateTransaction(transactionEntity.id, request)
                            if (updateResponse.isSuccessful) {
                                // После успешного обновления, обновляем lastUpdatedAt и isSynced
                                val updatedEntity = transactionEntity.copy(
                                    isSynced = true,
                                    lastUpdatedAt = System.currentTimeMillis() // Можно взять с ответа сервера, если он возвращает
                                )
                                transactionDao.upsert(updatedEntity)
                                Log.d("SyncWorker", "Updated transaction ${transactionEntity.id} on server.")
                            } else {
                                Log.e("SyncWorker", "Failed to update transaction ${transactionEntity.id} on server. Code: ${updateResponse.code()}")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("SyncWorker", "Error processing update for transaction ${transactionEntity.id}", e)
                    }
                }
            }
        }
    }

    private suspend fun refreshAllDataFromServer() {
        Log.d("SyncWorker", "Refreshing all data from server...")
        try {
            val accountsResponse = apiService.getAccounts()
            if (accountsResponse.isSuccessful) {
                accountsResponse.body()?.let { dtos ->
                    val entities = dtos.map { it.toDomainModel().toEntity() }
                    accountDao.upsertAll(entities)
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
        } catch(e: Exception) {
            Log.e("SyncWorker", "Error refreshing data from server", e)
        }
    }

    private fun parseTimestamp(dateString: String): Long? {
        return try {
            Instant.parse(dateString).toEpochMilli()
        } catch (e: DateTimeParseException) {
            null
        }
    }
}