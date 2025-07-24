package com.myfinances.ui.mappers

import com.myfinances.R
import com.myfinances.domain.entity.HapticEffect
import com.myfinances.ui.model.HapticsUiModel
import com.myfinances.ui.util.ResourceProvider
import javax.inject.Inject

class HapticEffectDomainToUiMapper @Inject constructor(
    private val resourceProvider: ResourceProvider
) {
    fun map(effect: HapticEffect, isSelected: Boolean): HapticsUiModel {
        return HapticsUiModel(
            effect = effect,
            name = getEffectName(effect),
            isSelected = isSelected
        )
    }

    private fun getEffectName(effect: HapticEffect): String {
        return resourceProvider.getString(
            when (effect) {
                HapticEffect.CLICK -> R.string.haptic_effect_click
                HapticEffect.DOUBLE_CLICK -> R.string.haptic_effect_double_click
                HapticEffect.TICK -> R.string.haptic_effect_tick
            }
        )
    }
}