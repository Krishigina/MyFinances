package com.myfinances.ui.mappers

import com.myfinances.R
import com.myfinances.domain.entity.Language
import com.myfinances.ui.model.LanguageUiModel
import com.myfinances.ui.util.ResourceProvider
import javax.inject.Inject

class LanguageDomainToUiMapper @Inject constructor(
    private val resourceProvider: ResourceProvider
) {
    fun map(language: Language, isSelected: Boolean): LanguageUiModel {
        return LanguageUiModel(
            language = language,
            name = getLanguageName(language),
            isSelected = isSelected
        )
    }

    private fun getLanguageName(language: Language): String {
        return resourceProvider.getString(
            when (language) {
                Language.RUSSIAN -> R.string.language_russian
                Language.ENGLISH -> R.string.language_english
            }
        )
    }

    fun mapToName(language: Language): String {
        return getLanguageName(language)
    }
}