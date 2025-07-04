package com.myfinances.data.store

import kotlinx.coroutines.flow.Flow

/**
 * Абстракция для управления данными пользовательской сессии.
 * Определяет контракт для сохранения и получения ID активного счета.
 */
interface SessionStore {
    /**
     * Возвращает Flow с ID сохраненного счета или null, если он не был сохранен.
     */
    val activeAccountId: Flow<Int?>

    /**
     * Сохраняет ID активного счета в персистентное хранилище.
     */
    suspend fun setAccountId(id: Int)
}