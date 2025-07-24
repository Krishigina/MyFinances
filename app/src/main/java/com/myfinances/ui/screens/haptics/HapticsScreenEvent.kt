package com.myfinances.ui.screens.haptics

import com.myfinances.domain.entity.HapticEffect

sealed interface HapticsScreenEvent {
    data class OnHapticsToggled(val enabled: Boolean) : HapticsScreenEvent
    data class OnEffectSelected(val effect: HapticEffect) : HapticsScreenEvent
}