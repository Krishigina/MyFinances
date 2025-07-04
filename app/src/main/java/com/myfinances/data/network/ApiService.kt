package com.myfinances.data.network

import com.google.gson.annotations.SerializedName
import com.myfinances.data.network.dto.AccountDto
import com.myfinances.data.network.dto.CategoryDto
import com.myfinances.data.network.dto.TransactionDto
import com.myfinances.data.network.dto.UpdateAccountRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Определяет эндпоинты REST API для взаимодействия с бэкендом.
 * Используется Retrofit для создания сетевых запросов.
 */
interface ApiService {

    @GET("accounts")
    suspend fun getAccounts(): Response<List<AccountDto>>

    @GET("accounts/{id}")
    suspend fun getAccountById(@Path("id") accountId: Int): Response<AccountDto>

    @PUT("accounts/{id}")
    suspend fun updateAccount(
        @Path("id") accountId: Int,
        @Body request: UpdateAccountRequest
    ): Response<AccountDto>

    @GET("categories")
    suspend fun getCategories(): Response<List<CategoryDto>>

    @POST("transactions")
    suspend fun createTransaction(@Body transaction: CreateTransactionRequest): Response<TransactionDto>

    @GET("transactions/account/{accountId}/period")
    suspend fun getTransactionsForPeriod(
        @Path("accountId") accountId: Int,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<List<TransactionDto>>
}

data class CreateTransactionRequest(
    @SerializedName("accountId") val accountId: Int,
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("amount") val amount: String,
    @SerializedName("transactionDate") val transactionDate: String,
    @SerializedName("comment") val comment: String?
)