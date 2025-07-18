package com.myfinances.data.repository

import com.myfinances.data.db.dao.TransactionDao
import com.myfinances.data.db.entity.toEntity
import com.myfinances.data.network.ApiService
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.data.network.CreateTransactionRequest
import com.myfinances.data.network.dto.UpdateTransactionRequest
import com.myfinances.data.network.dto.toDomainModel
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
            return Result.NetworkError
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

    override suspend fun getTransactionById(transactionId: Int): Result<Transaction> {
        // Сначала пытаемся получить из локальной базы
        val localTransaction = transactionDao.getTransactionById(transactionId)
        if (localTransaction != null) {
            return Result.Success(localTransaction.toDomainModel())
        }

        // Если нет в базе, идем в сеть (может быть полезно для синхронизации)
        if (!connectivityManager.isNetworkAvailable.first()) {
            return Result.Error(Exception("Transaction not found locally and no network"))
        }

        return try {
            val response = apiService.getTransactionById(transactionId)
            if (response.isSuccessful && response.body() != null) {
                val domainModel = response.body()!!.toDomainModel()
                if (domainModel != null) {
                    transactionDao.upsert(domainModel.toEntity())
                    Result.Success(domainModel)
                } else {
                    Result.Error(IllegalStateException("Failed to parse transaction data"))
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

    override suspend fun createTransaction(
        accountId: Int,
        categoryId: Int,
        amount: Double,
        transactionDate: Date,
        comment: String
    ): Result<Transaction> {
        // Создаем транзакцию с временным отрицательным ID, чтобы избежать конфликтов
        // с серверными ID. И помечаем её как несинхронизированную.
        val temporaryId = (System.currentTimeMillis() * -1) + Random.nextInt()

        val newTransaction = Transaction(
            id = temporaryId.toInt(),
            accountId = accountId,
            categoryId = categoryId,
            amount = amount,
            date = transactionDate,
            comment = comment
        )

        transactionDao.upsert(newTransaction.toEntity(isSynced = false))

        // TODO: Запустить воркер для синхронизации

        return Result.Success(newTransaction)
    }

    override suspend fun updateTransaction(
        transactionId: Int,
        accountId: Int,
        categoryId: Int,
        amount: Double,
        transactionDate: Date,
        comment: String
    ): Result<Transaction> {
        val updatedTransaction = Transaction(
            id = transactionId,
            accountId = accountId,
            categoryId = categoryId,
            amount = amount,
            date = transactionDate,
            comment = comment
        )

        // Обновляем локальную запись и помечаем её как несинхронизированную
        transactionDao.upsert(updatedTransaction.toEntity(isSynced = false))

        // TODO: Запустить воркер для синхронизации

        return Result.Success(updatedTransaction)
    }

    override suspend fun deleteTransaction(transactionId: Int): Result<Unit> {
        val transaction = transactionDao.getTransactionById(transactionId)
            ?: return Result.Error(Exception("Transaction not found"))

        // Если транзакция была создана оффлайн и никогда не была на сервере
        // (отрицательный ID), просто удаляем ее из локальной базы.
        if (transaction.id < 0) {
            transactionDao.deleteById(transaction.id)
        } else {
            // Иначе, помечаем для "мягкого" удаления и последующей синхронизации
            val updatedTransaction = transaction.copy(
                isDeletedLocally = true,
                isSynced = false
            )
            transactionDao.upsert(updatedTransaction)
        }

        // TODO: Запустить воркер для синхронизации

        return Result.Success(Unit)
    }
}