package com.myfinances.ui.viewmodel

import androidx.compose.material3.SnackbarHostState

data class ScaffoldState(
    val topBarState: TopBarState = TopBarState(),
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val isFabVisible: Boolean = false,
    val isBottomBarVisible: Boolean = false
)