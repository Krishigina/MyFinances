package com.myfinances.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.myfinances.data.db.dao.TransactionDao
import com.myfinances.data.db.entity.toEntity
import com.myfinances.data.network.ApiService
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.data.network.CreateTransactionRequest
import com.myfinances.data.network.dto.UpdateTransactionRequest
import com.myfinances.data.network.dto.toDomainModel
import com.myfinances.data.workers.SyncWorker
import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.repository.TransactionsRepository
import com.myfinances.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import kotlin.random.Random


class TransactionsRepositoryImpl @Inject constructor(
    private val context: Context,
    private val apiService: ApiService,
    private val transactionDao: TransactionDao,
    private val connectivityManager: ConnectivityManagerSource
) : TransactionsRepository {

    private val apiDateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

    override fun getTransactions(
        accountId: Int,
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>> {
        return transactionDao.getTransactionsForPeriod(accountId, startDate.time, endDate.time)
            .map { entities ->
                entities.map { it.toDomainModel() }
            }
    }

    override suspend fun refreshTransactions(
        accountId: Int,
        startDate: Date,
        endDate: Date
    ): Result<Unit> {
        if (!connectivityManager.isNetworkAvailable.first()) {
            return Result.Failure.NetworkError
        }

        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val response = apiService.getTransactionsForPeriod(
                accountId = accountId,
                startDate = dateFormat.format(startDate),
                endDate = dateFormat.format(endDate)
            )

            if (response.isSuccessful) {
                val dtos = response.body()
                if (dtos != null) {
                    val entities = dtos.mapNotNull { it.toDomainModel()?.toEntity() }
                    transactionDao.upsertAll(entities)
                    Result.Success(Unit)
                } else {
                    Result.Failure.GenericError(Exception("Empty response body"))
                }
            } else {
                Result.Failure.GenericError(Exception("API Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: IOException) {
            Result.Failure.NetworkError
        } catch (e: Exception) {
            Result.Failure.GenericError(e)
        }
    }

    override suspend fun getTransactionById(transactionId: Int): Result<Transaction> {
        val localTransaction = transactionDao.getTransactionById(transactionId)
        if (localTransaction != null) {
            return Result.Success(localTransaction.toDomainModel())
        }

        if (!connectivityManager.isNetworkAvailable.first()) {
            return Result.Failure.GenericError(Exception("Transaction not found locally and no network"))
        }

        return try {
            val response = apiService.getTransactionById(transactionId)
            if (response.isSuccessful && response.body() != null) {
                val domainModel = response.body()!!.toDomainModel()
                if (domainModel != null) {
                    transactionDao.upsert(domainModel.toEntity())
                    Result.Success(domainModel)
                } else {
                    Result.Failure.GenericError(IllegalStateException("Failed to parse transaction data"))
                }
            } else {
                Result.Failure.GenericError(Exception("API Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: IOException) {
            Result.Failure.NetworkError
        } catch (e: Exception) {
            Result.Failure.GenericError(e)
        }
    }

    override suspend fun createTransaction(
        accountId: Int,
        categoryId: Int,
        amount: Double,
        transactionDate: Date,
        comment: String
    ): Result<Transaction> {
        return try {
            val temporaryId = (System.currentTimeMillis() * -1) + Random.nextInt()

            val newTransaction = Transaction(
                id = temporaryId.toInt(),
                accountId = accountId,
                categoryId = categoryId,
                amount = amount,
                date = transactionDate,
                comment = comment,
                lastUpdatedAt = System.currentTimeMillis()
            )

            transactionDao.upsert(newTransaction.toEntity(isSynced = false))
            Result.Success(newTransaction)
        } catch (e: Exception) {
            Result.Failure.GenericError(e)
        }
    }

    override suspend fun updateTransaction(
        transactionId: Int,
        accountId: Int,
        categoryId: Int,
        amount: Double,
        transactionDate: Date,
        comment: String
    ): Result<Transaction> {
        return try {
            val updatedTransaction = Transaction(
                id = transactionId,
                accountId = accountId,
                categoryId = categoryId,
                amount = amount,
                date = transactionDate,
                comment = comment,
                lastUpdatedAt = System.currentTimeMillis()
            )

            transactionDao.upsert(updatedTransaction.toEntity(isSynced = false))
            Result.Success(updatedTransaction)
        } catch (e: Exception) {
            Result.Failure.GenericError(e)
        }
    }

    override suspend fun deleteTransaction(transactionId: Int): Result<Unit> {
        return try {
            val transaction = transactionDao.getTransactionById(transactionId)
                ?: return Result.Failure.GenericError(Exception("Transaction not found"))

            if (transaction.id < 0) { 
                transactionDao.deleteById(transaction.id)
            } else {
                val updatedTransaction = transaction.copy(
                    isDeletedLocally = true,
                    isSynced = false
                )
                transactionDao.upsert(updatedTransaction)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure.GenericError(e)
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