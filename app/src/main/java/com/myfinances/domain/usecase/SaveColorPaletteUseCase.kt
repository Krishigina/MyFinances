package com.myfinances.domain.usecase

import com.myfinances.domain.entity.ColorPalette
import com.myfinances.domain.repository.SessionRepository
import javax.inject.Inject

class SaveColorPaletteUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(palette: ColorPalette) {
        sessionRepository.setColorPalette(palette)
    }
}