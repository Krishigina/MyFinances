package com.myfinances.domain.usecase

import com.myfinances.domain.entity.ThemeSetting
import com.myfinances.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<ThemeSetting> = sessionRepository.getTheme()
}