package com.myfinances.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.entity.ThemeSetting
import com.myfinances.domain.usecase.GetColorPaletteUseCase
import com.myfinances.domain.usecase.GetCurrentLanguageUseCase
import com.myfinances.domain.usecase.GetSyncFrequencyUseCase
import com.myfinances.domain.usecase.GetThemeUseCase
import com.myfinances.domain.usecase.IsPinSetUseCase
import com.myfinances.domain.usecase.SaveThemeUseCase
import com.myfinances.ui.mappers.ColorPaletteDomainToUiMapper
import com.myfinances.ui.mappers.LanguageDomainToUiMapper
import com.myfinances.ui.mappers.SyncFrequencyDomainToUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
open class SettingsViewModel @Inject constructor(
    getThemeUseCase: GetThemeUseCase,
    private val saveThemeUseCase: SaveThemeUseCase,
    getColorPaletteUseCase: GetColorPaletteUseCase,
    isPinSetUseCase: IsPinSetUseCase,
    private val getCurrentLanguageUseCase: GetCurrentLanguageUseCase,
    getSyncFrequencyUseCase: GetSyncFrequencyUseCase,
    private val colorPaletteMapper: ColorPaletteDomainToUiMapper,
    private val languageMapper: LanguageDomainToUiMapper,
    private val syncFrequencyMapper: SyncFrequencyDomainToUiMapper
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        combine(
            getThemeUseCase(),
            getColorPaletteUseCase(),
            isPinSetUseCase(),
            getSyncFrequencyUseCase()
        ) { theme, palette, isPinSet, syncFrequency ->
            _uiState.update {
                it.copy(
                    isDarkMode = theme == ThemeSetting.DARK,
                    currentPaletteName = colorPaletteMapper.mapToName(palette),
                    isPinSet = isPinSet,
                    currentSyncFrequencyName = syncFrequencyMapper.map(syncFrequency)
                )
            }
        }.launchIn(viewModelScope)

        setCurrentLanguage()
    }

    private fun setCurrentLanguage() {
        val language = getCurrentLanguageUseCase()
        _uiState.update {
            it.copy(currentLanguageName = languageMapper.mapToName(language))
        }
    }

    open fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnThemeToggled -> {
                viewModelScope.launch {
                    val newTheme = if (event.isEnabled) ThemeSetting.DARK else ThemeSetting.LIGHT
                    saveThemeUseCase(newTheme)
                }
            }
        }
    }

    fun onResume() {
        setCurrentLanguage()
    }
}