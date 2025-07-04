package com.myfinances.data.manager

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A singleton manager to broadcast events about account updates.
 * Other ViewModels can listen to these events to refresh their data.
 */
@Singleton
class AccountUpdateManager @Inject constructor() {
    private val _accountUpdateFlow = MutableSharedFlow<Unit>()
    val accountUpdateFlow = _accountUpdateFlow.asSharedFlow()

    suspend fun notifyAccountUpdated() {
        _accountUpdateFlow.emit(Unit)
    }
}