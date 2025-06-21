package com.myfinances.di

import com.myfinances.data.store.SessionStore
import com.myfinances.domain.repository.MyFinancesRepository
import com.myfinances.domain.usecase.GetAccountsUseCase
import com.myfinances.domain.usecase.GetActiveAccountIdUseCase
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.usecase.GetExpenseTransactionsUseCase
import com.myfinances.domain.usecase.GetIncomeTransactionsUseCase
import com.myfinances.domain.usecase.GetTransactionsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {

    @Provides
    @ViewModelScoped
    fun provideGetTransactionsUseCase(repository: MyFinancesRepository): GetTransactionsUseCase {
        return GetTransactionsUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetCategoriesUseCase(repository: MyFinancesRepository): GetCategoriesUseCase {
        return GetCategoriesUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetExpenseTransactionsUseCase(repository: MyFinancesRepository): GetExpenseTransactionsUseCase {
        return GetExpenseTransactionsUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetIncomeTransactionsUseCase(repository: MyFinancesRepository): GetIncomeTransactionsUseCase {
        return GetIncomeTransactionsUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetActiveAccountIdUseCase(
        repository: MyFinancesRepository,
        sessionStore: SessionStore
    ): GetActiveAccountIdUseCase {
        return GetActiveAccountIdUseCase(repository, sessionStore)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAccountsUseCase(repository: MyFinancesRepository): GetAccountsUseCase {
        return GetAccountsUseCase(repository)
    }
}