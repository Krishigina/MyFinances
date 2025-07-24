package com.myfinances.domain.usecase

import com.myfinances.data.manager.LocaleManager
import com.myfinances.domain.entity.Language
import javax.inject.Inject

class SaveLanguageUseCase @Inject constructor(
    private val localeManager: LocaleManager
) {
    suspend operator fun invoke(language: Language) {
        localeManager.updateAppLocale(language.code)
    }
}