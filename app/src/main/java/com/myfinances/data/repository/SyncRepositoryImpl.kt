package com.myfinances.data.repository

import android.util.Log
import com.myfinances.data.db.dao.AccountDao
import com.myfinances.data.db.dao.CategoryDao
import com.myfinances.data.db.dao.TransactionDao
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
import kotlinx.coroutines.joinAll
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
    private val sessionRepository: SessionRepository
) : SyncRepository {

    private val apiDateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

    override suspend fun syncData(): Result<Unit> {
        if (!connectivityManager.isNetworkAvailable.first()) {
            return Result.Failure.NetworkError
        }

        return try {
            coroutineScope {
                Log.d("SyncWorker", "Starting data synchronization...")

                val syncLocalJob = launch { syncLocalChangesToServer() }
                syncLocalJob.join()

                val refreshJob = launch { refreshAllDataFromServer() }
                refreshJob.join()
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
        Log.d("SyncWorker", "Phase 1: Syncing local changes to server.")
        coroutineScope {
            val jobs = listOf(
                launch { syncUnsyncedAccounts() },
                launch { syncUnsyncedTransactions() }
            )
            jobs.joinAll()
        }
        Log.d("SyncWorker", "Phase 1 finished.")
    }

    private suspend fun syncUnsyncedAccounts() {
        val unsyncedAccounts = accountDao.getUnsyncedAccounts()
        if (unsyncedAccounts.isEmpty()) {
            Log.d("SyncWorker", "No unsynced accounts found.")
            return
        }

        Log.d("SyncWorker", "Found ${unsyncedAccounts.size} unsynced accounts.")
        for (accountEntity in unsyncedAccounts) {
            try {
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
            } catch (e: Exception) {
                Log.e("SyncWorker", "Error syncing account ${accountEntity.id}", e)
            }
        }
    }

    private suspend fun syncUnsyncedTransactions() {
        val unsyncedTransactions = transactionDao.getUnsyncedTransactions()
        if (unsyncedTransactions.isEmpty()) {
            Log.d("SyncWorker", "No unsynced transactions found.")
            return
        }
        Log.d("SyncWorker", "Found ${unsyncedTransactions.size} unsynced transactions.")

        for (transaction in unsyncedTransactions) {
            try {
                when {
                    transaction.isDeletedLocally -> {
                        val response = apiService.deleteTransaction(transaction.id)
                        if (response.isSuccessful) {
                            transactionDao.deleteById(transaction.id)
                            Log.d("SyncWorker", "Deleted transaction ${transaction.id} on server and locally.")
                        } else {
                            Log.e("SyncWorker", "Failed to delete transaction ${transaction.id} on server. Code: ${response.code()}")
                        }
                    }
                    transaction.id < 0 -> {
                        val request = CreateTransactionRequest(
                            accountId = transaction.accountId,
                            categoryId = transaction.categoryId ?: 0,
                            amount = transaction.amount.toString(),
                            transactionDate = apiDateFormat.format(transaction.date),
                            comment = transaction.comment
                        )
                        val response = apiService.createTransaction(request)
                        if (response.isSuccessful && response.body() != null) {
                            val newTransactionDto = response.body()!!
                            transactionDao.deleteById(transaction.id)
                            newTransactionDto.toDomainModel()?.let {
                                transactionDao.upsert(it.toEntity(isSynced = true))
                            }
                            Log.d("SyncWorker", "Created new transaction on server. Local temp id ${transaction.id} -> new id ${newTransactionDto.id}.")
                        } else {
                            Log.e("SyncWorker", "Failed to create transaction ${transaction.id} on server. Code: ${response.code()}")
                        }
                    }
                    else -> {
                        val request = UpdateTransactionRequest(
                            accountId = transaction.accountId,
                            categoryId = transaction.categoryId ?: 0,
                            amount = transaction.amount.toString(),
                            transactionDate = apiDateFormat.format(transaction.date),
                            comment = transaction.comment
                        )
                        val response = apiService.updateTransaction(transaction.id, request)
                        if (response.isSuccessful && response.body() != null) {
                            val updatedDto = response.body()!!
                            updatedDto.toDomainModel()?.let {
                                transactionDao.upsert(it.toEntity(isSynced = true))
                            }
                            Log.d("SyncWorker", "Updated transaction ${transaction.id} on server.")
                        } else {
                            Log.e("SyncWorker", "Failed to update transaction ${transaction.id} on server. Code: ${response.code()}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SyncWorker", "Error syncing transaction ${transaction.id}", e)
            }
        }
    }

    private suspend fun refreshAllDataFromServer() {
        Log.d("SyncWorker", "Phase 2: Refreshing all data from server.")
        try {
            refreshAccountsAndCategories()

            transactionDao.clearAll()
            val accounts = accountDao.getAccounts().first()

            coroutineScope {
                val jobs = accounts.map { account ->
                    launch {
                        try {
                            val calendar = Calendar.getInstance()
                            val endDate = calendar.time
                            calendar.add(Calendar.MONTH, -3)
                            val startDate = calendar.time
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                            val response = apiService.getTransactionsForPeriod(
                                accountId = account.id,
                                startDate = dateFormat.format(startDate),
                                endDate = dateFormat.format(endDate)
                            )
                            if (response.isSuccessful) {
                                response.body()?.let { dtos ->
                                    val entities = dtos.mapNotNull { it.toDomainModel()?.toEntity(isSynced = true) }
                                    transactionDao.upsertAll(entities)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("SyncWorker", "Failed to refresh transactions for account ${account.id}", e)
                        }
                    }
                }
                jobs.joinAll()
            }

            Log.d("SyncWorker", "Refreshed accounts, categories, and recent transactions.")
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error refreshing data from server", e)
            throw e
        }
    }

    private suspend fun refreshAccountsAndCategories() {
        coroutineScope {
            val accountJob = launch {
                val response = apiService.getAccounts()
                if (response.isSuccessful) {
                    response.body()?.let { dtos ->
                        val entities = dtos.map { it.toDomainModel().toEntity(isSynced = true) }
                        accountDao.upsertAll(entities)
                    }
                }
            }
            val categoryJob = launch {
                val response = apiService.getCategories()
                if (response.isSuccessful) {
                    response.body()?.let { dtos ->
                        val entities = dtos.map { it.toDomainModel().toEntity() }
                        categoryDao.upsertAll(entities)
                    }
                }
            }
            listOf(accountJob, categoryJob).joinAll()
        }
    }
}