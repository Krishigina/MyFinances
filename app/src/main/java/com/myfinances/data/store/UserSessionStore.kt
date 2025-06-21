package com.myfinances.data.store

import javax.inject.Inject
import javax.inject.Singleton

interface SessionStore {
    fun getAccountId(): Int?
    fun setAccountId(id: Int)
}

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