package com.myfinances.domain.usecase

import com.myfinances.domain.repository.PinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsPinSetUseCase @Inject constructor(
    private val pinRepository: PinRepository
) {
    operator fun invoke(): Flow<Boolean> = pinRepository.isPinSet()
}