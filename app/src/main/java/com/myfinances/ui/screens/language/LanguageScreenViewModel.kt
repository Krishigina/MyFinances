package com.myfinances.ui.screens.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import com.myfinances.data.manager.LocaleManager
import com.myfinances.domain.entity.Language
import com.myfinances.ui.mappers.LanguageDomainToUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class LanguageScreenViewModel @Inject constructor(
    private val localeManager: LocaleManager,
    private val mapper: LanguageDomainToUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        val allLanguages = Language.entries
        val currentAppLocales = AppCompatDelegate.getApplicationLocales()
        val currentLangCode = if (!currentAppLocales.isEmpty) {
            currentAppLocales.get(0)?.toLanguageTag()
        } else {
            LocaleListCompat.getAdjustedDefault().get(0)?.toLanguageTag()
        }

        val currentLanguage = Language.fromCode(currentLangCode)

        _uiState.update {
            it.copy(
                languages = allLanguages.map { lang ->
                    mapper.map(lang, isSelected = lang == currentLanguage)
                }
            )
        }
    }

    fun onEvent(event: LanguageScreenEvent) {
        when (event) {
            is LanguageScreenEvent.OnLanguageSelected -> {
                localeManager.updateAppLocale(event.language.code)
                _uiState.update {
                    it.copy(
                        languages = it.languages.map { model ->
                            model.copy(isSelected = model.language == event.language)
                        }
                    )
                }
            }
        }
    }
}