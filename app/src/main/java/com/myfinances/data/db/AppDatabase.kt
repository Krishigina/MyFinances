package com.myfinances.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.myfinances.data.db.dao.AccountDao
import com.myfinances.data.db.dao.CategoryDao
import com.myfinances.data.db.dao.TransactionDao
import com.myfinances.data.db.entity.AccountEntity
import com.myfinances.data.db.entity.CategoryEntity
import com.myfinances.data.db.entity.TransactionEntity

@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
}