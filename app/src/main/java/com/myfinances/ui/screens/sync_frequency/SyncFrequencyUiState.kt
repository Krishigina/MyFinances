package com.myfinances.ui.screens.sync_frequency

import com.myfinances.domain.entity.SyncFrequency

data class SyncFrequencyUiState(
    val currentFrequency: SyncFrequency = SyncFrequency.default,
    val selectedFrequencyLabel: String = ""
)