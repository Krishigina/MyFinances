package com.myfinances.data.store

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Абстракция для управления данными пользовательской сессии.
 * Определяет контракт для сохранения и получения ID активного счета.
 */
interface SessionStore {
    /**
     * Возвращает ID сохраненного счета или null, если он не был сохранен.
     */
    fun getAccountId(): Int?

    /**
     * Сохраняет ID активного счета в памяти на время жизни приложения.
     */
    fun setAccountId(id: Int)
}

/**
 * Реализация [SessionStore], которая хранит ID счета в переменной в памяти.
 * Данные будут утеряны при полном перезапуске приложения.
 */
@Singleton
class UserSessionStore @Inject constructor() : SessionStore {
    private var accountId: Int? = null

    override fun getAccountId(): Int? {
        return accountId
    }

    override fun setAccountId(id: Int) {
        this.accountId = id
    }
}