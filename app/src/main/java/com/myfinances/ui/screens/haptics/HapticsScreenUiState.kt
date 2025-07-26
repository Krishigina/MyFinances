package com.myfinances.ui.screens.haptics

import com.myfinances.ui.model.HapticsUiModel

data class HapticsScreenUiState(
    val isEnabled: Boolean = true,
    val effects: List<HapticsUiModel> = emptyList()
)