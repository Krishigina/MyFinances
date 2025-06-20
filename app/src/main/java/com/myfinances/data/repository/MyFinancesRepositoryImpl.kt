package com.myfinances.data.repository

import com.myfinances.data.network.ApiService
import com.myfinances.data.network.dto.toDomainModel
import com.myfinances.domain.entity.Account
import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.repository.MyFinancesRepository
import com.myfinances.domain.util.Result
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyFinancesRepositoryImpl(
    private val apiService: ApiService
) : MyFinancesRepository {

    override suspend fun getTransactions(
        accountId: Int,
        startDate: Date,
        endDate: Date
    ): Result<List<Transaction>> {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val response = apiService.getTransactionsForPeriod(
                accountId = accountId,
                startDate = dateFormat.format(startDate),
                endDate = dateFormat.format(endDate)
            )

            if (response.isSuccessful) {
                val transactions = response.body()?.map { it.toDomainModel() } ?: emptyList()
                Result.Success(transactions)
            } else {
                Result.Error(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            val response = apiService.getCategories()
            if (response.isSuccessful) {
                val categories = response.body()?.map { it.toDomainModel() } ?: emptyList()
                Result.Success(categories)
            } else {
                Result.Error(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getAccounts(): Result<List<Account>> {
        TODO("Not yet implemented")
    }
}