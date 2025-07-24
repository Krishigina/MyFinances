package com.myfinances.ui.screens.settings

sealed interface SettingsEvent {
    data class OnThemeToggled(val isEnabled: Boolean) : SettingsEvent
}