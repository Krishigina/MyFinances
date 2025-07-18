package com.myfinances.di

import android.content.Context
import androidx.room.Room
import com.myfinances.data.db.AppDatabase
import com.myfinances.data.db.dao.AccountDao
import com.myfinances.data.db.dao.CategoryDao
import com.myfinances.data.db.dao.TransactionDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "my_finances.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAccountDao(database: AppDatabase): AccountDao = database.accountDao()

    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    @Singleton
    fun provideTransactionDao(database: AppDatabase): TransactionDao = database.transactionDao()
}