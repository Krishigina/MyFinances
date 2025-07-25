package com.myfinances.domain.usecase

import com.myfinances.domain.entity.SyncFrequency
import com.myfinances.domain.repository.SessionRepository
import javax.inject.Inject

class SaveSyncFrequencyUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val setupPeriodicSyncUseCase: SetupPeriodicSyncUseCase
) {
    suspend operator fun invoke(frequency: SyncFrequency) {
        sessionRepository.setSyncFrequency(frequency)
        setupPeriodicSyncUseCase()
    }
}