package com.myfinances.di

import com.myfinances.data.store.SessionStore
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.repository.TransactionsRepository
import com.myfinances.domain.usecase.GetAccountUseCase
import com.myfinances.domain.usecase.GetActiveAccountIdUseCase
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.usecase.GetTransactionsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt-модуль, предоставляющий зависимости для доменного слоя (UseCases).
 * Установлен в [ViewModelComponent] с областью видимости [ViewModelScoped],
 * что означает, что экземпляры UseCase'ов будут "жить" столько же, сколько
 * и соответствующая ViewModel, и будут пересоздаваться для каждой новой ViewModel.
 */
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
    fun provideGetActiveAccountIdUseCase(
        accountsRepository: AccountsRepository,
        sessionStore: SessionStore
    ): GetActiveAccountIdUseCase {
        return GetActiveAccountIdUseCase(accountsRepository, sessionStore)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAccountUseCase(repository: AccountsRepository): GetAccountUseCase {
        return GetAccountUseCase(repository)
    }
}