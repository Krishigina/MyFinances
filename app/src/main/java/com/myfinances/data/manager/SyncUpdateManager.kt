package com.myfinances.data.manager

import com.myfinances.domain.repository.SessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A singleton manager to broadcast events about completed synchronization.
 * ViewModels can listen to these events to show notifications like Snackbars.
 */
@Singleton
class SyncUpdateManager @Inject constructor(
    sessionRepository: SessionRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _syncCompletedFlow = MutableSharedFlow<Long>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val syncCompletedFlow = _syncCompletedFlow.asSharedFlow()
    private var lastKnownSyncTime: Long? = null

    init {
        scope.launch {
            // Отслеживаем изменения времени последней синхронизации
            sessionRepository.getLastSyncTime()
                .distinctUntilChanged()
                .collect { newTime ->
                    // Мы отправляем событие, только если время изменилось,
                    // и это не первая инициализация (когда lastKnownSyncTime еще null).
                    if (newTime != null && lastKnownSyncTime != null && newTime != lastKnownSyncTime) {
                        _syncCompletedFlow.tryEmit(newTime)
                    }
                    lastKnownSyncTime = newTime
                }
        }
    }
}