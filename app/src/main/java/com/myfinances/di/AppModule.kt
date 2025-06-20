package com.myfinances.di

import com.myfinances.data.network.ApiService
import com.myfinances.data.network.RetrofitInstance
import com.myfinances.data.repository.MyFinancesRepositoryImpl
import com.myfinances.domain.repository.MyFinancesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitInstance.api
    }

    @Provides
    @Singleton
    fun provideMyFinancesRepository(api: ApiService): MyFinancesRepository {
        return MyFinancesRepositoryImpl(api)
    }
}