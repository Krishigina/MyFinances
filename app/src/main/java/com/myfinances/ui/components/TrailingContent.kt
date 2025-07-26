package com.myfinances.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable

sealed class TrailingContent {
    data class TextWithArrow(
        val text: String,
        val secondaryText: String? = null
    ) : TrailingContent()

    data class TextOnly(val text: String) : TrailingContent()
    data class Switch(val isChecked: Boolean, val onToggle: (Boolean) -> Unit) : TrailingContent()
    data class ArrowOnly(@DrawableRes val customIconRes: Int? = null) : TrailingContent()
    data class Custom(val content: @Composable () -> Unit) : TrailingContent()
}