package com.myfinances.data.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

/**
 * Перехватчик для OkHttp, который автоматически повторяет сетевые запросы в случае сбоев.
 * Срабатывает при получении серверных ошибок (коды 5xx) или при возникновении `IOException`
 * (например, обрыв соединения). Делает несколько попыток с небольшой задержкой между ними,
 * чтобы повысить шансы на успешное выполнение запроса при временных проблемах с сетью или сервером.
 */
class RetryInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var exception: IOException? = null

        var tryCount = 0
        while (tryCount < MAX_RETRIES && (response == null || !response.isSuccessful && response.code >= 500)) {
            response?.close()

            try {
                response = chain.proceed(request)

                if (response.code >= 500) {
                    tryCount++
                    if (tryCount < MAX_RETRIES) {
                        Thread.sleep(RETRY_DELAY_MS)
                    }
                }
            } catch (e: IOException) {
                exception = e
                tryCount++
                if (tryCount < MAX_RETRIES) {
                    Thread.sleep(RETRY_DELAY_MS)
                }
            }
        }

        if (response == null && exception != null) {
            throw exception
        }

        return response!!
    }

    companion object {
        private const val MAX_RETRIES = 3
        private const val RETRY_DELAY_MS = 2000L
    }
}