package com.myfinances.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.entity.ThemeSetting
import com.myfinances.domain.usecase.GetColorPaletteUseCase
import com.myfinances.domain.usecase.GetThemeUseCase
import com.myfinances.domain.usecase.SaveThemeUseCase
import com.myfinances.ui.mappers.ColorPaletteDomainToUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    getThemeUseCase: GetThemeUseCase,
    private val saveThemeUseCase: SaveThemeUseCase,
    getColorPaletteUseCase: GetColorPaletteUseCase,
    private val colorPaletteMapper: ColorPaletteDomainToUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val themeFlow = getThemeUseCase()
        val paletteFlow = getColorPaletteUseCase()

        combine(themeFlow, paletteFlow) { theme, palette ->
            _uiState.update {
                it.copy(
                    isDarkMode = theme == ThemeSetting.DARK,
                    currentPaletteName = colorPaletteMapper.mapToName(palette)
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnThemeToggled -> {
                viewModelScope.launch {
                    val newTheme = if (event.isEnabled) ThemeSetting.DARK else ThemeSetting.LIGHT
                    saveThemeUseCase(newTheme)
                }
            }
        }
    }
}