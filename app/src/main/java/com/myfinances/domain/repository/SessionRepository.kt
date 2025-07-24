package com.myfinances.domain.repository

import com.myfinances.domain.entity.ColorPalette
import com.myfinances.domain.entity.HapticEffect
import com.myfinances.domain.entity.ThemeSetting
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun getActiveAccountId(): Flow<Int?>
    suspend fun setActiveAccountId(id: Int)

    fun getLastSyncTime(): Flow<Long?>
    suspend fun setLastSyncTime(time: Long)

    fun getTheme(): Flow<ThemeSetting>
    suspend fun setTheme(theme: ThemeSetting)

    fun getColorPalette(): Flow<ColorPalette>
    suspend fun setColorPalette(palette: ColorPalette)

    fun getHapticsEnabled(): Flow<Boolean>
    suspend fun setHapticsEnabled(enabled: Boolean)

    fun getHapticEffect(): Flow<HapticEffect>
    suspend fun setHapticEffect(effect: HapticEffect)
}