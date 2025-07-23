package com.myfinances.domain.util

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    sealed class Failure(val error: Throwable? = null) : Result<Nothing>() {

        data class ApiError(val code: Int, val message: String) : Failure()

        data class GenericError(val exception: Throwable) : Failure(exception)

        data object NetworkError : Failure()
    }
}