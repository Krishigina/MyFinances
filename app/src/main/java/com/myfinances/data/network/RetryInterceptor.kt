package com.myfinances.data.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

/**
 * Перехватывает и автоматически повторяет сетевые запросы в случае возникновения
 * серверных ошибок (коды 5xx) или проблем с подключением.
 */
class RetryInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("TaskCancellation", "Starting network request for ${chain.request().url}")
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