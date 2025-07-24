package com.myfinances.domain.usecase

import com.myfinances.domain.entity.ThemeSetting
import com.myfinances.domain.repository.SessionRepository
import javax.inject.Inject

class SaveThemeUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(theme: ThemeSetting) {
        sessionRepository.setTheme(theme)
    }
}