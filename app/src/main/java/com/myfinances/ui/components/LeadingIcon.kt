package com.myfinances.ui.components

import androidx.annotation.DrawableRes

sealed class LeadingIcon {
    data class Resource(@DrawableRes val id: Int) : LeadingIcon()
    data class Emoji(val char: String) : LeadingIcon()
}