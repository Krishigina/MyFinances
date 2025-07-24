package com.myfinances.ui.screens.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.entity.Language
import com.myfinances.domain.usecase.GetCurrentLanguageUseCase
import com.myfinances.domain.usecase.SaveLanguageUseCase
import com.myfinances.ui.mappers.LanguageDomainToUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class LanguageScreenViewModel @Inject constructor(
    private val getCurrentLanguageUseCase: GetCurrentLanguageUseCase,
    private val saveLanguageUseCase: SaveLanguageUseCase,
    private val mapper: LanguageDomainToUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        val allLanguages = Language.entries
        val currentLanguage = getCurrentLanguageUseCase()

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
                viewModelScope.launch {
                    saveLanguageUseCase(event.language)
                }
            }
        }
    }

    /**
     * Вызывается, когда экран снова становится видимым, чтобы обновить состояние
     * выбранного языка после системной смены локали.
     */
    fun onResume() {
        loadLanguages()
    }
}