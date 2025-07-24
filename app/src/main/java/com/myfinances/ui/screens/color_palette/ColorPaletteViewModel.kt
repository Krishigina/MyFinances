package com.myfinances.ui.screens.color_palette

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.entity.ColorPalette
import com.myfinances.domain.usecase.GetColorPaletteUseCase
import com.myfinances.domain.usecase.SaveColorPaletteUseCase
import com.myfinances.ui.mappers.ColorPaletteDomainToUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ColorPaletteViewModel @Inject constructor(
    private val getColorPaletteUseCase: GetColorPaletteUseCase,
    private val saveColorPaletteUseCase: SaveColorPaletteUseCase,
    private val mapper: ColorPaletteDomainToUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ColorPaletteUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val allPalettes = ColorPalette.entries
        val currentPaletteFlow = getColorPaletteUseCase()

        combine(currentPaletteFlow, MutableStateFlow(allPalettes)) { current, all ->
            all.map { palette ->
                mapper.map(palette, isSelected = palette == current)
            }
        }.onEach { palettes ->
            _uiState.update { it.copy(palettes = palettes) }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: ColorPaletteEvent) {
        when (event) {
            is ColorPaletteEvent.OnPaletteSelected -> {
                viewModelScope.launch {
                    saveColorPaletteUseCase(event.palette)
                }
            }
        }
    }
}