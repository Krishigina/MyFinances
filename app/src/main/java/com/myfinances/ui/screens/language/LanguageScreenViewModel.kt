package com.myfinances.ui.screens.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.entity.Language
import com.myfinances.domain.usecase.GetLanguageUseCase
import com.myfinances.domain.usecase.SaveLanguageUseCase
import com.myfinances.ui.mappers.LanguageDomainToUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class LanguageScreenViewModel @Inject constructor(
    getLanguageUseCase: GetLanguageUseCase,
    private val saveLanguageUseCase: SaveLanguageUseCase,
    private val mapper: LanguageDomainToUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val allLanguages = Language.entries
        val currentLanguageFlow = getLanguageUseCase()

        combine(currentLanguageFlow, MutableStateFlow(allLanguages)) { current, all ->
            all.map { language ->
                mapper.map(language, isSelected = language == current)
            }
        }.onEach { languages ->
            _uiState.update { it.copy(languages = languages) }
        }.launchIn(viewModelScope)
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
}