package com.myfinances.ui.screens.common

interface UiEvent
sealed interface CommonEvent : UiEvent {
    data object Refresh : CommonEvent
}