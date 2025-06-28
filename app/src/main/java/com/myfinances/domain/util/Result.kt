package com.myfinances.domain.util

/**
 * Герметичный класс для представления результата асинхронной операции.
 * Позволяет обрабатывать успех, ошибку и отсутствие сети в явном виде.
 *
 * @param T Тип данных в случае успеха.
 */
sealed class Result<out T> {
    /**
     * Успешный результат, содержит данные.
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Ошибка выполнения, содержит исключение.
     */
    data class Error(val exception: Throwable) : Result<Nothing>()

    /**
     * Ошибка сети, указывает на проблемы с интернет-соединением.
     */
    data object NetworkError : Result<Nothing>()
}