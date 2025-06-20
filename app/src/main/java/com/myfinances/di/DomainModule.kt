// file: di/DomainModule.kt
package com.myfinances.di

import com.myfinances.domain.repository.MyFinancesRepository
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.usecase.GetTransactionsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {

    @Provides
    fun provideGetTransactionsUseCase(repository: MyFinancesRepository): GetTransactionsUseCase {
        return GetTransactionsUseCase(repository)
    }

    @Provides
    fun provideGetCategoriesUseCase(repository: MyFinancesRepository): GetCategoriesUseCase {
        return GetCategoriesUseCase(repository)
    }
}