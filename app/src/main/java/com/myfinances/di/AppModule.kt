package com.myfinances.di

import com.myfinances.BuildConfig
import com.myfinances.data.network.ApiService
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.data.network.NetworkConnectivityManager
import com.myfinances.data.network.RetryInterceptor
import com.myfinances.data.repository.AccountsRepositoryImpl
import com.myfinances.data.repository.CategoriesRepositoryImpl
import com.myfinances.data.repository.TransactionsRepositoryImpl
import com.myfinances.data.store.SessionStore
import com.myfinances.data.store.UserSessionStore
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.repository.TransactionsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConnectivityModule {
    @Binds
    @Singleton
    abstract fun bindConnectivityManager(
        networkConnectivityManager: NetworkConnectivityManager
    ): ConnectivityManagerSource
}

@Module
@InstallIn(SingletonComponent::class)
abstract class StoreModule {
    @Binds
    @Singleton
    abstract fun bindSessionStore(
        userSessionStore: UserSessionStore
    ): SessionStore
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAccountsRepository(impl: AccountsRepositoryImpl): AccountsRepository

    @Binds
    @Singleton
    abstract fun bindCategoriesRepository(impl: CategoriesRepositoryImpl): CategoriesRepository

    @Binds
    @Singleton
    abstract fun bindTransactionsRepository(impl: TransactionsRepositoryImpl): TransactionsRepository
}


@Module
@InstallIn(SingletonComponent::class)
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
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://shmr-finance.ru/api/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}