package com.myfinances.ui.screens.language

import com.myfinances.domain.entity.Language

sealed interface LanguageScreenEvent {
    data class OnLanguageSelected(val language: Language) : LanguageScreenEvent
}