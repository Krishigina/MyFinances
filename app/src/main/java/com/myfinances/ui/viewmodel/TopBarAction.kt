package com.myfinances.ui.viewmodel

import androidx.compose.runtime.Composable

data class TopBarAction(
    val id: String,
    val isEnabled: Boolean = true,
    val onAction: () -> Unit,
    val content: @Composable () -> Unit
)