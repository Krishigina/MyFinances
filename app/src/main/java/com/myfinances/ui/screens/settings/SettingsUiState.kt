package com.myfinances.ui.screens.settings

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val currentPaletteName: String = "",
    val currentLanguageName: String = "",
    val isPinSet: Boolean = false,
    val currentSyncFrequencyName: String = ""
)