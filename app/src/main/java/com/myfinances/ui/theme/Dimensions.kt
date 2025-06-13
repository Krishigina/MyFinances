package com.myfinances.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Dimensions(
    val spacing: Spacing = Spacing(),
    val icon: Icon = Icon()
)

@Immutable
data class Spacing(
    val paddingSmall: Dp = 4.dp,
    val paddingMedium: Dp = 8.dp,
    val paddingExtraMedium: Dp = 14.dp,
    val paddingLarge: Dp = 16.dp,
    val paddingExtraLarge: Dp = 22.dp
)

@Immutable
data class Icon(
    val medium: Dp = 24.dp
)

val defaultDimensions = Dimensions()

val LocalDimensions = staticCompositionLocalOf { defaultDimensions }
