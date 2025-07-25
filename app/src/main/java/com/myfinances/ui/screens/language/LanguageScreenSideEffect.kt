package com.myfinances.ui.screens.language

sealed interface LanguageScreenSideEffect {
    data object RecreateActivity : LanguageScreenSideEffect
}