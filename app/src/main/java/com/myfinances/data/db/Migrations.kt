package com.myfinances.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Объект-контейнер для миграций базы данных Room.
 */
object Migrations {
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE accounts ADD COLUMN lastUpdatedAt INTEGER NOT NULL DEFAULT 0")
        }
    }
}