package com.myfinances.domain.repository

import com.myfinances.domain.entity.ThemeSetting
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun getActiveAccountId(): Flow<Int?>
    suspend fun setActiveAccountId(id: Int)

    fun getLastSyncTime(): Flow<Long?>
    suspend fun setLastSyncTime(time: Long)

    fun getTheme(): Flow<ThemeSetting>
    suspend fun setTheme(theme: ThemeSetting)
}