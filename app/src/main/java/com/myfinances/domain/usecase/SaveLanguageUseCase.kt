package com.myfinances.domain.usecase

import com.myfinances.data.manager.LocaleManager
import com.myfinances.domain.entity.Language
import com.myfinances.domain.repository.SessionRepository
import javax.inject.Inject

class SaveLanguageUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val localeManager: LocaleManager
) {
    suspend operator fun invoke(language: Language) {
        sessionRepository.setLanguage(language)
        localeManager.updateAppLocale(language.code)
    }
}