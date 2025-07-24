package com.myfinances.ui.model

import com.myfinances.domain.entity.HapticEffect

data class HapticsUiModel(
    val effect: HapticEffect,
    val name: String,
    val isSelected: Boolean
)