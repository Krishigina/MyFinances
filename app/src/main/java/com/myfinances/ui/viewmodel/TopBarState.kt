package com.myfinances.ui.viewmodel

data class TopBarState(
    val title: String = "",
    val navigationAction: TopBarAction? = null,
    val actions: List<TopBarAction> = emptyList()
)