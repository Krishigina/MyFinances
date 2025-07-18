package com.myfinances.data.repository

import com.myfinances.data.network.ApiService
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.data.network.CreateTransactionRequest
import com.myfinances.data.network.dto.UpdateTransactionRequest
import com.myfinances.data.network.dto.toDomainModel
import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.repository.TransactionsRepository
import com.myfinances.domain.util.Result
import kotlinx.coroutines.flow.first
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

/**
 * Реализация интерфейса [TransactionsRepository] из доменного слоя.
 * Отвечает за получение списка транзакций для конкретного счета за заданный
 * временной период из удаленного API.
 */
class TransactionsRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val connectivityManager: ConnectivityManagerSource
) : BaseRepository(connectivityManager), TransactionsRepository {

    private val apiDateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

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

    override suspend fun getTransactionById(transactionId: Int): Result<Transaction> {
        return when (val result = safeApiCall { apiService.getTransactionById(transactionId) }) {
            is Result.Success -> {
                result.data.toDomainModel()?.let {
                    Result.Success(it)
                } ?: Result.Error(IllegalStateException("Failed to parse transaction data"))
            }
            is Result.Error -> result
            is Result.NetworkError -> result
        }
    }

    override suspend fun createTransaction(
        accountId: Int,
        categoryId: Int,
        amount: Double,
        transactionDate: Date,
        comment: String
    ): Result<Transaction> {
        val request = CreateTransactionRequest(
            accountId = accountId,
            categoryId = categoryId,
            amount = amount.toString(),
            transactionDate = apiDateFormat.format(transactionDate),
            comment = comment
        )
        return when (val result = safeApiCall { apiService.createTransaction(request) }) {
            is Result.Success -> {
                result.data.toDomainModel()?.let {
                    Result.Success(it)
                }
                    ?: Result.Error(IllegalStateException("Failed to parse transaction data after creation"))
            }
            is Result.Error -> result
            is Result.NetworkError -> result
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
        val request = UpdateTransactionRequest(
            accountId = accountId,
            categoryId = categoryId,
            amount = amount.toString(),
            transactionDate = apiDateFormat.format(transactionDate),
            comment = comment
        )
        return when (val result =
            safeApiCall { apiService.updateTransaction(transactionId, request) }) {
            is Result.Success -> {
                result.data.toDomainModel()?.let {
                    Result.Success(it)
                }
                    ?: Result.Error(IllegalStateException("Failed to parse transaction data after update"))
            }

            is Result.Error -> result
            is Result.NetworkError -> result
        }
    }

    override suspend fun deleteTransaction(transactionId: Int): Result<Unit> {
        // Проверяем доступность сети перед выполнением запроса
        if (!connectivityManager.isNetworkAvailable.first()) {
            return Result.NetworkError
        }
        return try {
            val response = apiService.deleteTransaction(transactionId)
            // Для DELETE запросов успешный ответ (например, 204 No Content) не содержит тела,
            // но isSuccessful будет true. Мы должны вернуть Success(Unit) в этом случае.
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                // Если сервер вернул ошибку, создаем Result.Error
                Result.Error(Exception("Error deleting transaction: ${response.code()} ${response.message()}"))
            }
        } catch (e: IOException) {
            // Отлавливаем сетевые исключения
            Result.NetworkError
        } catch (e: Exception) {
            // Отлавливаем другие возможные исключения
            Result.Error(e)
        }
    }
}