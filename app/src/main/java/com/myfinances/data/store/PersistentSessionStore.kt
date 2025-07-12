package com.myfinances.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
}