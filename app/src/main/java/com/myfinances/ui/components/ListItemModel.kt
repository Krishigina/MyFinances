package com.myfinances.ui.components

import androidx.annotation.DrawableRes

data class ListItemModel(
    val id: String,
    val title: String,
    val type: ItemType,
    val leadingIcon: LeadingIcon? = null,
    val subtitle: String? = null,
    val trailingContent: TrailingContent? = null,
    val useWhiteIconBackground: Boolean = false,
    val showTrailingArrow: Boolean = true,
    @DrawableRes val trailingArrowIconRes: Int? = null,
    val onClick: (() -> Unit)? = null,
    val trailingContentTestTag: String? = null
)