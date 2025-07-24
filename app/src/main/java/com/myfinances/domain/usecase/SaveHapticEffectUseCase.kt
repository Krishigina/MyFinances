package com.myfinances.domain.usecase

import com.myfinances.domain.entity.HapticEffect
import com.myfinances.domain.repository.SessionRepository
import javax.inject.Inject

class SaveHapticEffectUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(effect: HapticEffect) {
        sessionRepository.setHapticEffect(effect)
    }
}