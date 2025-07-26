package com.myfinances.domain.repository

import kotlinx.coroutines.flow.Flow

interface PinRepository {
    fun isPinSet(): Flow<Boolean>
    suspend fun savePin(pin: String)
    suspend fun verifyPin(pin: String): Boolean
    suspend fun deletePin()
}