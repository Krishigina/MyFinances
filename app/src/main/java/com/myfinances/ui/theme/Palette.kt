package com.myfinances.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import com.myfinances.domain.entity.ColorPalette

private val GreenLightColorScheme = lightColorScheme(
    primary = Green_Primary,
    secondary = Green_Secondary,
    tertiary = LightGrey,
    secondaryContainer = Green_Secondary,
    onSecondaryContainer = Green_Primary,
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

private val PinkLightColorScheme = lightColorScheme(
    primary = Pink_Primary,
    secondary = Pink_Secondary,
    tertiary = LightGrey,
    secondaryContainer = Pink_Secondary,
    onSecondaryContainer = Pink_Primary,
    background = WhiteBackground,
    surface = ExtraLightGrey,
    surfaceContainer = ExtraLightGrey,
    onPrimary = WhiteBackground,
    onSecondary = BrightBlack,
    onBackground = BrightBlack,
    onSurface = BrightBlack,
    onSurfaceVariant = LightBlack,
    surfaceTint = ExtraLightGrey,
    onTertiary = LightBlack,
    outlineVariant = GreyDivider
)

private val BlueLightColorScheme = lightColorScheme(
    primary = Blue_Primary,
    secondary = Blue_Secondary,
    tertiary = LightGrey,
    secondaryContainer = Blue_Secondary,
    onSecondaryContainer = Blue_Primary,
    background = WhiteBackground,
    surface = ExtraLightGrey,
    surfaceContainer = ExtraLightGrey,
    onPrimary = WhiteBackground,
    onSecondary = BrightBlack,
    onBackground = BrightBlack,
    onSurface = BrightBlack,
    onSurfaceVariant = LightBlack,
    surfaceTint = ExtraLightGrey,
    onTertiary = LightBlack,
    outlineVariant = GreyDivider
)

private val BeigeLightColorScheme = lightColorScheme(
    primary = Beige_Primary,
    secondary = Beige_Secondary,
    tertiary = LightGrey,
    secondaryContainer = Beige_Secondary,
    onSecondaryContainer = Beige_Primary,
    background = WhiteBackground,
    surface = ExtraLightGrey,
    surfaceContainer = ExtraLightGrey,
    onPrimary = WhiteBackground,
    onSecondary = BrightBlack,
    onBackground = BrightBlack,
    onSurface = BrightBlack,
    onSurfaceVariant = LightBlack,
    surfaceTint = ExtraLightGrey,
    onTertiary = LightBlack,
    outlineVariant = GreyDivider
)

private val GreenDarkColorScheme = darkColorScheme(
    primary = Green_DarkPrimary,
    onPrimary = Green_DarkOnPrimary,
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
    onSecondaryContainer = Green_DarkOnSecondaryContainer,
    outline = DarkThemeOutline,
    outlineVariant = DarkThemeOutline
)

private val PinkDarkColorScheme = darkColorScheme(
    primary = Pink_DarkPrimary,
    onPrimary = Pink_DarkOnPrimary,
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
    onSecondaryContainer = Pink_DarkOnSecondaryContainer,
    outline = DarkThemeOutline,
    outlineVariant = DarkThemeOutline
)

private val BlueDarkColorScheme = darkColorScheme(
    primary = Blue_DarkPrimary,
    onPrimary = Blue_DarkOnPrimary,
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
    onSecondaryContainer = Blue_DarkOnSecondaryContainer,
    outline = DarkThemeOutline,
    outlineVariant = DarkThemeOutline
)

private val BeigeDarkColorScheme = darkColorScheme(
    primary = Beige_DarkPrimary,
    onPrimary = Beige_DarkOnPrimary,
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
    onSecondaryContainer = Beige_DarkOnSecondaryContainer,
    outline = DarkThemeOutline,
    outlineVariant = DarkThemeOutline
)

internal fun provideLightColorPalette(palette: ColorPalette) = when (palette) {
    ColorPalette.GREEN -> GreenLightColorScheme
    ColorPalette.PINK -> PinkLightColorScheme
    ColorPalette.BLUE -> BlueLightColorScheme
    ColorPalette.BEIGE -> BeigeLightColorScheme
}

internal fun provideDarkColorPalette(palette: ColorPalette) = when (palette) {
    ColorPalette.GREEN -> GreenDarkColorScheme
    ColorPalette.PINK -> PinkDarkColorScheme
    ColorPalette.BLUE -> BlueDarkColorScheme
    ColorPalette.BEIGE -> BeigeDarkColorScheme
}