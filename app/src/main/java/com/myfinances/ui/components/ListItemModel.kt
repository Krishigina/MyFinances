package com.myfinances.ui.components

data class ListItemModel(
    val id: String,
    val leadingIcon: LeadingIcon? = null,
    val title: String,
    val type: ItemType,
    val subtitle: String? = null,
    val trailingContent: TrailingContent? = null,
    val onClick: () -> Unit = {}
)