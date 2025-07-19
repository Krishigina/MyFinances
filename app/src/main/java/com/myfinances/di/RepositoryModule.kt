package com.myfinances.di

import com.myfinances.data.repository.AccountsRepositoryImpl
import com.myfinances.data.repository.CategoriesRepositoryImpl
import com.myfinances.data.repository.SyncRepositoryImpl
import com.myfinances.data.repository.TransactionsRepositoryImpl
import com.myfinances.data.store.PersistentSessionStore
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.repository.CategoriesRepository
import com.myfinances.domain.repository.SessionRepository
import com.myfinances.domain.repository.SyncRepository
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

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: PersistentSessionStore): SessionRepository

    @Binds
    @Singleton
    abstract fun bindSyncRepository(impl: SyncRepositoryImpl): SyncRepository
}