package com.myfinances.ui.screens.sync_frequency

import com.myfinances.domain.entity.SyncFrequency

sealed interface SyncFrequencyEvent {
    data class OnFrequencySelected(val frequency: SyncFrequency) : SyncFrequencyEvent
}