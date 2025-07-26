package com.myfinances.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.myfinances.domain.repository.PinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinRepositoryImpl @Inject constructor(
    private val context: Context
) : PinRepository {

    private val pinStateFlow = MutableStateFlow(false)

    private val sharedPreferences by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            "pin_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    init {
        pinStateFlow.value = sharedPreferences.contains(PIN_HASH_KEY)
    }

    override fun isPinSet(): Flow<Boolean> = pinStateFlow

    override suspend fun savePin(pin: String) = withContext(Dispatchers.IO) {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        val saltString = salt.toHexString()

        val hash = hashString(pin + saltString)

        sharedPreferences.edit()
            .putString(PIN_HASH_KEY, hash)
            .putString(PIN_SALT_KEY, saltString)
            .commit()

        pinStateFlow.value = true
    }

    override suspend fun verifyPin(pin: String): Boolean = withContext(Dispatchers.IO) {
        val savedHash = sharedPreferences.getString(PIN_HASH_KEY, null)
        val savedSalt = sharedPreferences.getString(PIN_SALT_KEY, null)

        if (savedHash == null || savedSalt == null) {
            return@withContext false
        }

        val inputHash = hashString(pin + savedSalt)
        inputHash == savedHash
    }

    override suspend fun deletePin() = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .remove(PIN_HASH_KEY)
            .remove(PIN_SALT_KEY)
            .commit()
        pinStateFlow.value = false
    }

    private fun hashString(input: String): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())
            .toHexString()
    }

    private fun ByteArray.toHexString(): String {
        return this.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val PIN_HASH_KEY = "pin_hash"
        private const val PIN_SALT_KEY = "pin_salt"
    }
}