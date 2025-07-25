package com.myfinances.ui.screens.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myfinances.R
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TrailingContent
import com.myfinances.ui.navigation.PinMode

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToColorPalette: () -> Unit,
    onNavigateToHaptics: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    onNavigateToPin: (PinMode) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val pinAction = if (uiState.isPinSet) PinMode.DISABLE else PinMode.SETUP

    val settingsItems = listOf(
        ListItemModel(
            id = "dark_theme",
            type = ItemType.SETTING,
            title = stringResource(id = R.string.dark_theme),
            trailingContent = TrailingContent.Switch(
                isChecked = uiState.isDarkMode,
                onToggle = { isEnabled ->
                    viewModel.onEvent(SettingsEvent.OnThemeToggled(isEnabled))
                }
            ),
            showTrailingArrow = false
        ),
        ListItemModel(
            id = "primary_color",
            type = ItemType.SETTING,
            title = stringResource(id = R.string.primary_color),
            trailingContent = TrailingContent.TextWithArrow(
                text = uiState.currentPaletteName
            ),
            showTrailingArrow = true,
            onClick = onNavigateToColorPalette
        ),
        ListItemModel(
            id = "sounds",
            type = ItemType.SETTING,
            title = stringResource(id = R.string.sounds),
            trailingContent = TrailingContent.ArrowOnly(customIconRes = R.drawable.ic_settings_arrow),
            showTrailingArrow = false
        ),
        ListItemModel(
            id = "haptics",
            type = ItemType.SETTING,
            title = stringResource(id = R.string.haptics),
            trailingContent = TrailingContent.ArrowOnly(customIconRes = R.drawable.ic_settings_arrow),
            showTrailingArrow = false,
            onClick = onNavigateToHaptics
        ),
        ListItemModel(
            id = "passcode",
            type = ItemType.SETTING,
            title = stringResource(id = R.string.passcode),
            trailingContent = TrailingContent.TextWithArrow(
                text = if (uiState.isPinSet) stringResource(R.string.pin_status_on) else stringResource(R.string.pin_status_off)
            ),
            showTrailingArrow = true,
            onClick = { onNavigateToPin(pinAction) }
        ),
        ListItemModel(
            id = "sync",
            type = ItemType.SETTING,
            title = stringResource(id = R.string.sync),
            trailingContent = TrailingContent.ArrowOnly(customIconRes = R.drawable.ic_settings_arrow),
            showTrailingArrow = false
        ),
        ListItemModel(
            id = "language",
            type = ItemType.SETTING,
            title = stringResource(id = R.string.language),
            trailingContent = TrailingContent.TextWithArrow(
                text = uiState.currentLanguageName
            ),
            showTrailingArrow = true,
            onClick = onNavigateToLanguage
        ),
        ListItemModel(
            id = "about",
            type = ItemType.SETTING,
            title = stringResource(id = R.string.about),
            trailingContent = TrailingContent.ArrowOnly(customIconRes = R.drawable.ic_settings_arrow),
            showTrailingArrow = false
        )
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            items = settingsItems,
            key = { it.id }
        ) { model ->
            ListItem(model = model)
            Divider()
        }
    }
}