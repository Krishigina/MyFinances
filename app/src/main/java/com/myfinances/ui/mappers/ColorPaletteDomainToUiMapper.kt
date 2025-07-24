package com.myfinances.ui.mappers

import com.myfinances.R
import com.myfinances.domain.entity.ColorPalette
import com.myfinances.ui.model.ColorPaletteUiModel
import com.myfinances.ui.theme.Beige_Primary
import com.myfinances.ui.theme.Blue_Primary
import com.myfinances.ui.theme.Green_Primary
import com.myfinances.ui.theme.Pink_Primary
import com.myfinances.ui.util.ResourceProvider
import javax.inject.Inject

class ColorPaletteDomainToUiMapper @Inject constructor(
    private val resourceProvider: ResourceProvider
) {
    fun map(palette: ColorPalette, isSelected: Boolean): ColorPaletteUiModel {
        return ColorPaletteUiModel(
            palette = palette,
            name = getPaletteName(palette),
            color = getPaletteColor(palette),
            isSelected = isSelected
        )
    }

    private fun getPaletteName(palette: ColorPalette): String {
        return resourceProvider.getString(
            when (palette) {
                ColorPalette.GREEN -> R.string.palette_green
                ColorPalette.PINK -> R.string.palette_pink
                ColorPalette.BLUE -> R.string.palette_blue
                ColorPalette.BEIGE -> R.string.palette_beige
            }
        )
    }

    private fun getPaletteColor(palette: ColorPalette) = when (palette) {
        ColorPalette.GREEN -> Green_Primary
        ColorPalette.PINK -> Pink_Primary
        ColorPalette.BLUE -> Blue_Primary
        ColorPalette.BEIGE -> Beige_Primary
    }

    fun mapToName(palette: ColorPalette): String {
        return getPaletteName(palette)
    }
}