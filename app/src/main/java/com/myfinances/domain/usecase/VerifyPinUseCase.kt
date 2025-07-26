package com.myfinances.domain.usecase

import com.myfinances.domain.repository.PinRepository
import javax.inject.Inject

class VerifyPinUseCase @Inject constructor(
    private val pinRepository: PinRepository
) {
    suspend operator fun invoke(pin: String): Boolean {
        return pinRepository.verifyPin(pin)
    }
}