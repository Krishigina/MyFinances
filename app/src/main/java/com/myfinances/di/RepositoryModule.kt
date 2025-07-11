package com.myfinances.di

import com.myfinances.data.repository.AccountsRepositoryImpl
import com.myfinances.data.repository.CategoriesRepositoryImpl
import com.myfinances.data.repository.TransactionsRepositoryImpl
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.repository.TransactionsRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAccountsRepository(impl: AccountsRepositoryImpl): AccountsRepository

    @Binds
    @Singleton
    abstract fun bindCategoriesRepository(impl: CategoriesRepositoryImpl): CategoriesRepository

    @Binds
    @Singleton
    abstract fun bindTransactionsRepository(impl: TransactionsRepositoryImpl): TransactionsRepository
}