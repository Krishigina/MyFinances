package com.myfinances.ui.screens.pin

sealed interface PinScreenEvent {
    data class OnNumberClick(val number: String) : PinScreenEvent
    data object OnBackspaceClick : PinScreenEvent
}