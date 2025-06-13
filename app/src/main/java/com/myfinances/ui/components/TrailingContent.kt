package com.myfinances.ui.components

sealed class TrailingContent {
    data class TextWithArrow(val text: String) : TrailingContent()
    data class TextOnly(val text: String) : TrailingContent()
    data class Switch(val isChecked: Boolean, val onToggle: (Boolean) -> Unit) : TrailingContent()
    data object ArrowOnly : TrailingContent()
}