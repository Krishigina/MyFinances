package com.myfinances.ui.screens.color_palette

import com.myfinances.ui.model.ColorPaletteUiModel

data class ColorPaletteUiState(
    val palettes: List<ColorPaletteUiModel> = emptyList()
)