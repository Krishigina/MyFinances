package com.myfinances.ui.screens.pin
import androidx.annotation.StringRes
import com.myfinances.ui.navigation.PinMode
sealed interface PinScreenUiState {
    data object Loading : PinScreenUiState
    data class Success(
        val pinMode: PinMode = PinMode.SETUP,
        @StringRes val titleRes: Int,
        val enteredPin: String = "",
        val pinToConfirm: String = "",
        val error: String? = null,
        val isLocked: Boolean = false,
        val navigateToMain: Boolean = false,
        val navigateBack: Boolean = false
    ) : PinScreenUiState
}