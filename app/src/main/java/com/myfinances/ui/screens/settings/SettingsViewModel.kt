package com.myfinances.ui.screens.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.entity.Language
import com.myfinances.domain.entity.ThemeSetting
import com.myfinances.domain.usecase.GetColorPaletteUseCase
import com.myfinances.domain.usecase.GetThemeUseCase
import com.myfinances.domain.usecase.SaveThemeUseCase
import com.myfinances.ui.mappers.ColorPaletteDomainToUiMapper
import com.myfinances.ui.mappers.LanguageDomainToUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val context: Context, // Используем Context
    getThemeUseCase: GetThemeUseCase,
    private val saveThemeUseCase: SaveThemeUseCase,
    getColorPaletteUseCase: GetColorPaletteUseCase,
    private val colorPaletteMapper: ColorPaletteDomainToUiMapper,
    private val languageMapper: LanguageDomainToUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Слушаем изменения темы и палитры
        combine(
            getThemeUseCase(),
            getColorPaletteUseCase()
        ) { theme, palette ->
            _uiState.update {
                it.copy(
                    isDarkMode = theme == ThemeSetting.DARK,
                    currentPaletteName = colorPaletteMapper.mapToName(palette)
                )
            }
        }.launchIn(viewModelScope)

        setCurrentLanguage()

        val executor = ContextCompat.getMainExecutor(context)
        AppCompatDelegate.setOnApplicationLocalesChangedListener(executor) {
            setCurrentLanguage()
        }
    }

    private fun setCurrentLanguage() {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val currentLangCode = if (!currentLocales.isEmpty) {
            currentLocales[0]?.toLanguageTag()
        } else {
            LocaleListCompat.getAdjustedDefault()[0]?.toLanguageTag()
        }
        val language = Language.fromCode(currentLangCode)
        _uiState.update {
            it.copy(currentLanguageName = languageMapper.mapToName(language))
        }
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