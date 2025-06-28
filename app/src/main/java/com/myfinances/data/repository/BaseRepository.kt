package com.myfinances.data.repository

import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.domain.util.Result
import kotlinx.coroutines.flow.first
import retrofit2.Response
import java.io.IOException

/**
 * Базовый класс для репозиториев, предоставляющий общую логику
 * для выполнения безопасных сетевых вызовов.
 *
 * @param connectivityManager Источник данных о состоянии сетевого подключения.
 */
abstract class BaseRepository(
    private val connectivityManager: ConnectivityManagerSource
) {

    /**
     * Выполняет suspend-функцию [apiCall], оборачивая ее в блок try-catch
     * для обработки ошибок сети и API, а также проверяет доступность сети.
     *
     * @param T Тип успешного ответа от API.
     * @param apiCall suspend-лямбда, выполняющая сетевой запрос.
     * @return [Result] с данными в случае успеха или с ошибкой.
     */
    protected suspend fun <T : Any> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
        if (!connectivityManager.isNetworkAvailable.first()) {
            return Result.NetworkError
        }
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                // Используем !!, т.к. isSuccessful гарантирует наличие body.
                // Если body будет null при успехе, это исключительная ситуация, которая должна "упасть".
                Result.Success(response.body()!!)
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