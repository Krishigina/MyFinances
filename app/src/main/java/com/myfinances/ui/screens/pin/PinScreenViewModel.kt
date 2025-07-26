package com.myfinances.ui.screens.pin

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.R
import com.myfinances.domain.usecase.DeletePinUseCase
import com.myfinances.domain.usecase.SavePinUseCase
import com.myfinances.domain.usecase.VerifyPinUseCase
import com.myfinances.ui.navigation.PinMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

open class PinScreenViewModel @Inject constructor(
    private val savePinUseCase: SavePinUseCase,
    private val verifyPinUseCase: VerifyPinUseCase,
    private val deletePinUseCase: DeletePinUseCase
) : ViewModel() {

    private lateinit var pinMode: PinMode

    private val _uiState = MutableStateFlow<PinScreenUiState>(PinScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun initialize(mode: PinMode) {
        if (this::pinMode.isInitialized) return
        this.pinMode = mode
        _uiState.value = PinScreenUiState.Success(
            pinMode = pinMode,
            titleRes = getTitleForMode(pinMode)
        )
    }

    open fun onEvent(event: PinScreenEvent) {
        val currentState = _uiState.value
        if (currentState !is PinScreenUiState.Success || currentState.isLocked) return

        when (event) {
            is PinScreenEvent.OnNumberClick -> appendDigit(event.number)
            is PinScreenEvent.OnBackspaceClick -> removeLastDigit()
        }
    }

    private fun appendDigit(digit: String) {
        _uiState.update { state ->
            if (state is PinScreenUiState.Success && state.enteredPin.length < PIN_LENGTH) {
                state.copy(enteredPin = state.enteredPin + digit, error = null)
            } else {
                state
            }
        }

        val updatedState = _uiState.value
        if (updatedState is PinScreenUiState.Success && updatedState.enteredPin.length == PIN_LENGTH) {
            processPin()
        }
    }

    private fun removeLastDigit() {
        _uiState.update { state ->
            if (state is PinScreenUiState.Success && state.enteredPin.isNotEmpty()) {
                state.copy(enteredPin = state.enteredPin.dropLast(1), error = null)
            } else {
                state
            }
        }
    }

    private fun processPin() {
        _uiState.update { state ->
            if (state is PinScreenUiState.Success) {
                state.copy(isLocked = true)
            } else {
                state
            }
        }

        val currentState = _uiState.value
        if (currentState is PinScreenUiState.Success) {
            when (currentState.pinMode) {
                PinMode.SETUP -> handleSetup(currentState)
                PinMode.VERIFY -> handleVerify(currentState)
                PinMode.DISABLE -> handleDisable(currentState)
            }
        }
    }

    private fun handleSetup(currentState: PinScreenUiState.Success) {
        if (currentState.pinToConfirm.isEmpty()) {
            _uiState.update {
                if (it is PinScreenUiState.Success) {
                    it.copy(
                        pinToConfirm = it.enteredPin,
                        enteredPin = "",
                        isLocked = false,
                        titleRes = R.string.pin_confirm_title
                    )
                } else {
                    it
                }
            }
        } else {
            if (currentState.enteredPin == currentState.pinToConfirm) {
                viewModelScope.launch {
                    savePinUseCase(currentState.enteredPin)
                    _uiState.update {
                        if (it is PinScreenUiState.Success) it.copy(navigateBack = true) else it
                    }
                }
            } else {
                showErrorAndReset(R.string.pin_error_mismatch)
            }
        }
    }

    private fun handleVerify(currentState: PinScreenUiState.Success) {
        viewModelScope.launch {
            val isCorrect = verifyPinUseCase(currentState.enteredPin)
            if (isCorrect) {
                _uiState.update {
                    if (it is PinScreenUiState.Success) it.copy(navigateToMain = true) else it
                }
            } else {
                showErrorAndReset(R.string.pin_error_incorrect)
            }
        }
    }

    private fun handleDisable(currentState: PinScreenUiState.Success) {
        viewModelScope.launch {
            val isCorrect = verifyPinUseCase(currentState.enteredPin)
            if (isCorrect) {
                deletePinUseCase()
                _uiState.update {
                    if (it is PinScreenUiState.Success) it.copy(navigateBack = true) else it
                }
            } else {
                showErrorAndReset(R.string.pin_error_incorrect)
            }
        }
    }

    private fun showErrorAndReset(@StringRes errorRes: Int) {
        viewModelScope.launch {
            _uiState.update {
                if (it is PinScreenUiState.Success) it.copy(error = it.pinMode.name) else it
            }
//            delay(500)
            _uiState.update {
                if (it is PinScreenUiState.Success) {
                    it.copy(
                        enteredPin = "",
                        error = null,
                        isLocked = false,
                        titleRes = getTitleForMode(it.pinMode, true),
                        pinToConfirm = if (errorRes == R.string.pin_error_mismatch) "" else it.pinToConfirm
                    )
                } else {
                    it
                }
            }
        }
    }

    private fun getTitleForMode(mode: PinMode, isError: Boolean = false): Int {
        if (isError) {
            return R.string.pin_error_incorrect
        }
        return when (mode) {
            PinMode.SETUP -> R.string.pin_enter_title
            PinMode.VERIFY -> R.string.pin_verify_title
            PinMode.DISABLE -> R.string.pin_disable_title
        }
    }

    companion object {
        const val PIN_LENGTH = 4
    }
}