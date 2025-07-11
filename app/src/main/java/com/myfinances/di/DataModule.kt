package com.myfinances.di

import com.myfinances.data.network.ApiService
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.data.network.NetworkConnectivityManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
abstract class ConnectivityModule {
    @Binds
    @Singleton
    abstract fun bindConnectivityManager(
        networkConnectivityManager: NetworkConnectivityManager
    ): ConnectivityManagerSource
}

@Module(includes = [ConnectivityModule::class])
object DataModule {
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