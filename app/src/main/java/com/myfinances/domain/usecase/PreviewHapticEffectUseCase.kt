package com.myfinances.domain.usecase

import com.myfinances.data.manager.HapticFeedbackManager
import com.myfinances.domain.entity.HapticEffect
import javax.inject.Inject

class PreviewHapticEffectUseCase @Inject constructor(
    private val hapticFeedbackManager: HapticFeedbackManager
) {
    operator fun invoke(effect: HapticEffect) {
        hapticFeedbackManager.previewHapticEffect(effect)
    }
}