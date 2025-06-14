package com.myfinances.ui.components

data class ListItemModel(
    val id: String,
    val title: String,
    val type: ItemType,
    val leadingIcon: LeadingIcon? = null,
    val subtitle: String? = null,
    val trailingContent: TrailingContent? = null,
    val useWhiteIconBackground: Boolean = false,
    val showTrailingArrow: Boolean = true,
    val onClick: () -> Unit = {}
)