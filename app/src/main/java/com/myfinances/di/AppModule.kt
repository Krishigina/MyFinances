package com.myfinances.di

import com.myfinances.BuildConfig
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.data.network.RetryInterceptor
import com.myfinances.data.store.PersistentSessionStore
import com.myfinances.data.store.SessionStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
abstract class StoreModule {
    @Binds
    @Singleton
    abstract fun bindSessionStore(
        persistentSessionStore: PersistentSessionStore
    ): SessionStore
}

@Module
object AppModule {
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideRetryInterceptor(): RetryInterceptor {
        return RetryInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        retryInterceptor: RetryInterceptor
    ): OkHttpClient {
        val authInterceptor = okhttp3.Interceptor { chain ->
            val originalRequest = chain.request()
            val token = "Bearer ${BuildConfig.API_KEY}"
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", token)
                .build()
            chain.proceed(newRequest)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(retryInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideAccountUpdateManager(): AccountUpdateManager {
        return AccountUpdateManager()
    }
}