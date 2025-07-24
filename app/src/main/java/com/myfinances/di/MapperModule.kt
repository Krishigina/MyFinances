package com.myfinances.di

import com.myfinances.ui.mappers.AccountDomainToUiMapper
import com.myfinances.ui.mappers.AnalysisDomainToUiMapper
import com.myfinances.ui.mappers.CategoryDomainToUiMapper
import com.myfinances.ui.mappers.ColorPaletteDomainToUiMapper
import com.myfinances.ui.mappers.TransactionDomainToUiMapper
import com.myfinances.ui.util.ResourceProvider
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

    @Provides
    @Singleton
    fun provideAccountDomainToUiMapper(): AccountDomainToUiMapper {
        return AccountDomainToUiMapper()
    }

    @Provides
    @Singleton
    fun provideAnalysisDomainToUiMapper(): AnalysisDomainToUiMapper {
        return AnalysisDomainToUiMapper()
    }

    @Provides
    @Singleton
    fun provideColorPaletteDomainToUiMapper(resourceProvider: ResourceProvider): ColorPaletteDomainToUiMapper {
        return ColorPaletteDomainToUiMapper(resourceProvider)
    }
}