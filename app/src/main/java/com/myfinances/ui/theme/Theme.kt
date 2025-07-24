// app/src/main/java/com/myfinances/ui/theme/Theme.kt

package com.myfinances.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkThemePrimary,
    onPrimary = DarkThemeOnPrimary,
    secondary = DarkThemeSecondary,
    onSecondary = DarkThemeOnSecondary,
    tertiary = DarkThemeTertiary,
    onTertiary = DarkThemeOnSurface,
    background = DarkThemeBackground,
    onBackground = DarkThemeOnSurface,
    surface = DarkThemeSurface,
    onSurface = DarkThemeOnSurface,
    surfaceVariant = DarkThemeSurfaceVariant,
    onSurfaceVariant = DarkThemeOnSurface,
    secondaryContainer = DarkThemeSecondary,
    onSecondaryContainer = DarkThemeOnSecondaryContainer,
    outline = DarkThemeOutline,
    outlineVariant = DarkThemeOutline
)

private val LightColorScheme = lightColorScheme(
    primary = BrightGreen,
    secondary = PastelGreen,
    tertiary = LightGrey,
    secondaryContainer = PastelGreen,
    onSecondaryContainer = BrightGreen,
    background = WhiteBackground,
    surface = ExtraLightGrey,
    surfaceContainer = ExtraLightGrey,
    onPrimary = BrightBlack,
    onSecondary = BrightBlack,
    onBackground = BrightBlack,
    onSurface = BrightBlack,
    onSurfaceVariant = LightBlack,
    surfaceTint = ExtraLightGrey,
    onTertiary = LightBlack,
    outlineVariant = GreyDivider
)

@Composable
fun MyFinancesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val dimensions = defaultDimensions

    CompositionLocalProvider(LocalDimensions provides dimensions) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}