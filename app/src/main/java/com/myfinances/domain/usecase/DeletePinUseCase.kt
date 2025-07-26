package com.myfinances.domain.usecase

import com.myfinances.domain.repository.PinRepository
import javax.inject.Inject

class DeletePinUseCase @Inject constructor(
    private val pinRepository: PinRepository
) {
    suspend operator fun invoke() {
        pinRepository.deletePin()
    }
}