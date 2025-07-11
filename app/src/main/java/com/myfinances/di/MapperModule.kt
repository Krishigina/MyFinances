package com.myfinances.di

import com.myfinances.ui.mappers.CategoryDomainToUiMapper
import com.myfinances.ui.mappers.TransactionDomainToUiMapper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object MapperModule {

    @Provides
    @Singleton
    fun provideTransactionDomainToUiMapper(): TransactionDomainToUiMapper {
        return TransactionDomainToUiMapper()
    }

    @Provides
    @Singleton
    fun provideCategoryDomainToUiMapper(): CategoryDomainToUiMapper {
        return CategoryDomainToUiMapper()
    }
}