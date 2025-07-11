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
import com.myfinances.ui.mappers.CategoryDomainToUiMapper
import dagger.Module
import dagger.Provides

@Module
object DomainModule {

    @Provides
    @ViewModelScope
    fun provideGetTransactionsUseCase(
        transactionsRepository: TransactionsRepository,
        categoriesRepository: CategoriesRepository,
        accountsRepository: AccountsRepository,
        getActiveAccountIdUseCase: GetActiveAccountIdUseCase
    ): GetTransactionsUseCase {
        return GetTransactionsUseCase(
            transactionsRepository,
            categoriesRepository,
            accountsRepository,
            getActiveAccountIdUseCase
        )
    }

    @Provides
    @ViewModelScope
    fun provideGetCategoriesUseCase(
        repository: CategoriesRepository,
        mapper: CategoryDomainToUiMapper
    ): GetCategoriesUseCase {
        return GetCategoriesUseCase(repository, mapper)
    }

    @Provides
    @ViewModelScope
    fun provideGetActiveAccountIdUseCase(
        accountsRepository: AccountsRepository,
        sessionStore: SessionStore
    ): GetActiveAccountIdUseCase {
        return GetActiveAccountIdUseCase(accountsRepository, sessionStore)
    }

    @Provides
    @ViewModelScope
    fun provideGetAccountUseCase(
        repository: AccountsRepository,
        sessionStore: SessionStore
    ): GetAccountUseCase {
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