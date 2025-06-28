package com.myfinances.di

import com.myfinances.data.store.SessionStore
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.repository.TransactionsRepository
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
    fun provideGetTransactionsUseCase(
        transactionsRepository: TransactionsRepository,
        categoriesRepository: CategoriesRepository
    ): GetTransactionsUseCase {
        return GetTransactionsUseCase(transactionsRepository, categoriesRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetCategoriesUseCase(repository: CategoriesRepository): GetCategoriesUseCase {
        return GetCategoriesUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetExpenseTransactionsUseCase(
        transactionsRepository: TransactionsRepository,
        categoriesRepository: CategoriesRepository
    ): GetExpenseTransactionsUseCase {
        return GetExpenseTransactionsUseCase(transactionsRepository, categoriesRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetIncomeTransactionsUseCase(
        transactionsRepository: TransactionsRepository,
        categoriesRepository: CategoriesRepository
    ): GetIncomeTransactionsUseCase {
        return GetIncomeTransactionsUseCase(transactionsRepository, categoriesRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetActiveAccountIdUseCase(
        accountsRepository: AccountsRepository,
        sessionStore: SessionStore
    ): GetActiveAccountIdUseCase {
        return GetActiveAccountIdUseCase(accountsRepository, sessionStore)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAccountsUseCase(repository: AccountsRepository): GetAccountsUseCase {
        return GetAccountsUseCase(repository)
    }
}