package com.myfinances.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.myfinances.data.db.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Upsert
    suspend fun upsertAll(categories: List<CategoryEntity>)

    @Query("SELECT * FROM categories")
    fun getCategories(): Flow<List<CategoryEntity>>

    @Query("DELETE FROM categories")
    suspend fun clearAll()
}