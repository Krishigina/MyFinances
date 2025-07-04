package com.myfinances.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

sealed class LeadingIcon {
    data class Resource(@DrawableRes val id: Int) : LeadingIcon()
    data class Emoji(val char: String) : LeadingIcon()
    data class Vector(val imageVector: ImageVector) : LeadingIcon()
}