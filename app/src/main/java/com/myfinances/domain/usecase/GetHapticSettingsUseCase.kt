package com.myfinances.domain.usecase

import com.myfinances.domain.entity.HapticSettings
import com.myfinances.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetHapticSettingsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<HapticSettings> {
        return combine(
            sessionRepository.getHapticsEnabled(),
            sessionRepository.getHapticEffect()
        ) { isEnabled, effect ->
            HapticSettings(
                isEnabled = isEnabled,
                effect = effect
            )
        }
    }
}