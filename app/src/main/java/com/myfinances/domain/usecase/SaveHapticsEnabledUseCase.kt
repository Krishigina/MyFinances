package com.myfinances.domain.usecase

import com.myfinances.domain.repository.SessionRepository
import javax.inject.Inject

class SaveHapticsEnabledUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        sessionRepository.setHapticsEnabled(enabled)
    }
}