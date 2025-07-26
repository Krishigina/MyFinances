package com.myfinances.ui.viewmodel

data class ScaffoldState(
    val topBarState: TopBarState = TopBarState(),
    val isFabVisible: Boolean = false,
    val isBottomBarVisible: Boolean = false
)