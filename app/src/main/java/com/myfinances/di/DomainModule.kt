package com.myfinances.di

import com.myfinances.di.scopes.ViewModelScope
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.repository.SessionRepository
import com.myfinances.domain.repository.TransactionsRepository
import com.myfinances.domain.usecase.CreateTransactionUseCase
import com.myfinances.domain.usecase.DeleteTransactionUseCase
import com.myfinances.domain.usecase.GetAccountUseCase
import com.myfinances.domain.usecase.GetActiveAccountIdUseCase
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.usecase.GetExpenseTransactionsUseCase
import com.myfinances.domain.usecase.GetIncomeTransactionsUseCase
import com.myfinances.domain.usecase.GetTransactionDetailsUseCase
import com.myfinances.domain.usecase.GetTransactionsUseCase
import com.myfinances.domain.usecase.UpdateAccountUseCase
import com.myfinances.domain.usecase.UpdateTransactionUseCase
import dagger.Module
import dagger.Provides

@Module
class DomainModule {

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
        repository: CategoriesRepository
    ): GetCategoriesUseCase {
        return GetCategoriesUseCase(repository)
    }

    @Provides
    @ViewModelScope
    fun provideGetActiveAccountIdUseCase(
        accountsRepository: AccountsRepository,
        sessionRepository: SessionRepository
    ): GetActiveAccountIdUseCase {
        return GetActiveAccountIdUseCase(accountsRepository, sessionRepository)
    }

    @Provides
    @ViewModelScope
    fun provideGetAccountUseCase(
        repository: AccountsRepository,
        sessionRepository: SessionRepository
    ): GetAccountUseCase {
        return GetAccountUseCase(repository, sessionRepository)
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

    @Provides
    @ViewModelScope
    fun provideCreateTransactionUseCase(
        transactionsRepository: TransactionsRepository,
        getActiveAccountIdUseCase: GetActiveAccountIdUseCase
    ): CreateTransactionUseCase {
        return CreateTransactionUseCase(transactionsRepository, getActiveAccountIdUseCase)
    }

    @Provides
    @ViewModelScope
    fun provideUpdateTransactionUseCase(
        transactionsRepository: TransactionsRepository,
        getActiveAccountIdUseCase: GetActiveAccountIdUseCase
    ): UpdateTransactionUseCase {
        return UpdateTransactionUseCase(transactionsRepository, getActiveAccountIdUseCase)
    }

    @Provides
    @ViewModelScope
    fun provideGetTransactionDetailsUseCase(
        repository: TransactionsRepository
    ): GetTransactionDetailsUseCase {
        return GetTransactionDetailsUseCase(repository)
    }

    @Provides
    @ViewModelScope
    fun provideDeleteTransactionUseCase(
        repository: TransactionsRepository
    ): DeleteTransactionUseCase {
        return DeleteTransactionUseCase(repository)
    }
}