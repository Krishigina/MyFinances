package com.myfinances.ui.screens.color_palette

import com.myfinances.domain.entity.ColorPalette

sealed interface ColorPaletteEvent {
    data class OnPaletteSelected(val palette: ColorPalette) : ColorPaletteEvent
}