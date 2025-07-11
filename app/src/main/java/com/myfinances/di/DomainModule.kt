package com.myfinances.di

import com.myfinances.data.store.SessionStore
import com.myfinances.di.scopes.ViewModelScope
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.repository.TransactionsRepository
import com.myfinances.domain.usecase.GetAccountUseCase
import com.myfinances.domain.usecase.GetActiveAccountIdUseCase
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.usecase.GetExpenseTransactionsUseCase
import com.myfinances.domain.usecase.GetIncomeTransactionsUseCase
import com.myfinances.domain.usecase.GetTransactionsUseCase
import com.myfinances.domain.usecase.UpdateAccountUseCase
import dagger.Module
import dagger.Provides

/**
 * Dagger-модуль, предоставляющий зависимости для доменного слоя (UseCases).
 * Область видимости @ViewModelScope означает, что экземпляры UseCase'ов
 * будут "жить" столько же, сколько и ViewModelComponent, и будут
 * пересоздаваться для каждого нового компонента.
 */
@Module
object DomainModule {

    @Provides
    @ViewModelScope
    fun provideGetTransactionsUseCase(
        transactionsRepository: TransactionsRepository,
        categoriesRepository: CategoriesRepository
    ): GetTransactionsUseCase {
        return GetTransactionsUseCase(transactionsRepository, categoriesRepository)
    }

    @Provides
    @ViewModelScope
    fun provideGetCategoriesUseCase(repository: CategoriesRepository): GetCategoriesUseCase {
        return GetCategoriesUseCase(repository)
    }

    @Provides
    @ViewModelScope
    fun provideGetActiveAccountIdUseCase(
        accountsRepository: AccountsRepository,
        sessionStore: SessionStore // <-- ИСПРАВЛЕНИЕ: Добавлена зависимость
    ): GetActiveAccountIdUseCase {
        // Передаем обе зависимости в конструктор
        return GetActiveAccountIdUseCase(accountsRepository, sessionStore)
    }

    @Provides
    @ViewModelScope
    fun provideGetAccountUseCase(
        repository: AccountsRepository,
        sessionStore: SessionStore // <-- ИСПРАВЛЕНИЕ: Добавлена зависимость
    ): GetAccountUseCase {
        // Передаем обе зависимости в конструктор
        return GetAccountUseCase(repository, sessionStore)
    }

    @Provides
    @ViewModelScope
    fun provideGetExpenseTransactionsUseCase(
        getTransactionsUseCase: GetTransactionsUseCase
    ): GetExpenseTransactionsUseCase {
        return GetExpenseTransactionsUseCase(getTransactionsUseCase)
    }

    @Provides
    @ViewModelScope
    fun provideGetIncomeTransactionsUseCase(
        getTransactionsUseCase: GetTransactionsUseCase
    ): GetIncomeTransactionsUseCase {
        return GetIncomeTransactionsUseCase(getTransactionsUseCase)
    }

    @Provides
    @ViewModelScope
    fun provideUpdateAccountUseCase(repository: AccountsRepository): UpdateAccountUseCase {
        return UpdateAccountUseCase(repository)
    }
}