package com.myfinances.ui.viewmodel

import kotlinx.coroutines.flow.StateFlow

interface TopBarStateProvider {
    val topBarState: StateFlow<TopBarState>
}