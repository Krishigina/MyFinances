package com.myfinances.domain.usecase

import com.myfinances.domain.entity.SyncFrequency
import com.myfinances.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSyncFrequencyUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<SyncFrequency> = sessionRepository.getSyncFrequency()
}