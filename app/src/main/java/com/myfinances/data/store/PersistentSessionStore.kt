package com.myfinances.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.myfinances.domain.entity.ColorPalette
import com.myfinances.domain.entity.HapticEffect
import com.myfinances.domain.entity.SyncFrequency
import com.myfinances.domain.entity.ThemeSetting
import com.myfinances.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class PersistentSessionStore @Inject constructor(
    private val context: Context
) : SessionRepository {

    private companion object {
        val ACTIVE_ACCOUNT_ID = intPreferencesKey("active_account_id")
        val LAST_SYNC_TIME = longPreferencesKey("last_sync_time")
        val THEME_SETTING = stringPreferencesKey("theme_setting")
        val COLOR_PALETTE = stringPreferencesKey("color_palette")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val HAPTIC_EFFECT = stringPreferencesKey("haptic_effect")
        val SYNC_FREQUENCY_HOURS = longPreferencesKey("sync_frequency_hours")
    }

    override fun getActiveAccountId(): Flow<Int?> = context.dataStore.data
        .map { preferences ->
            preferences[ACTIVE_ACCOUNT_ID]
        }

    override suspend fun setActiveAccountId(id: Int) {
        context.dataStore.edit { settings ->
            settings[ACTIVE_ACCOUNT_ID] = id
        }
    }

    override fun getLastSyncTime(): Flow<Long?> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_SYNC_TIME]
        }

    override suspend fun setLastSyncTime(time: Long) {
        context.dataStore.edit { settings ->
            settings[LAST_SYNC_TIME] = time
        }
    }

    override fun getTheme(): Flow<ThemeSetting> = context.dataStore.data
        .map { preferences ->
            when (preferences[THEME_SETTING]) {
                ThemeSetting.DARK.name -> ThemeSetting.DARK
                else -> ThemeSetting.LIGHT
            }
        }

    override suspend fun setTheme(theme: ThemeSetting) {
        context.dataStore.edit { settings ->
            settings[THEME_SETTING] = theme.name
        }
    }

    override fun getColorPalette(): Flow<ColorPalette> = context.dataStore.data
        .map { preferences ->
            val paletteName = preferences[COLOR_PALETTE]
            try {
                if (paletteName != null) ColorPalette.valueOf(paletteName) else ColorPalette.default
            } catch (e: IllegalArgumentException) {
                ColorPalette.default
            }
        }

    override suspend fun setColorPalette(palette: ColorPalette) {
        context.dataStore.edit { settings ->
            settings[COLOR_PALETTE] = palette.name
        }
    }

    override fun getHapticsEnabled(): Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[HAPTICS_ENABLED] ?: true
        }

    override suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[HAPTICS_ENABLED] = enabled
        }
    }

    override fun getHapticEffect(): Flow<HapticEffect> = context.dataStore.data
        .map { preferences ->
            val effectName = preferences[HAPTIC_EFFECT]
            try {
                if (effectName != null) HapticEffect.valueOf(effectName) else HapticEffect.default
            } catch (e: IllegalArgumentException) {
                HapticEffect.default
            }
        }

    override suspend fun setHapticEffect(effect: HapticEffect) {
        context.dataStore.edit { settings ->
            settings[HAPTIC_EFFECT] = effect.name
        }
    }

    override fun getSyncFrequency(): Flow<SyncFrequency> = context.dataStore.data
        .map { preferences ->
            val hours = preferences[SYNC_FREQUENCY_HOURS] ?: SyncFrequency.default.hours
            SyncFrequency.fromHours(hours)
        }


    override suspend fun setSyncFrequency(frequency: SyncFrequency) {
        context.dataStore.edit { settings ->
            settings[SYNC_FREQUENCY_HOURS] = frequency.hours
        }
    }
}