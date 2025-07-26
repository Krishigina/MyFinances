package com.myfinances.ui.model

import androidx.compose.ui.graphics.Color
import com.myfinances.domain.entity.ColorPalette

data class ColorPaletteUiModel(
    val palette: ColorPalette,
    val name: String,
    val color: Color,
    val isSelected: Boolean
)