package com.myfinances.ui.model

import com.myfinances.domain.entity.Language

data class LanguageUiModel(
    val language: Language,
    val name: String,
    val isSelected: Boolean
)