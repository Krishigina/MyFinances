package com.myfinances.ui.screens.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.myfinances.R
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TrailingContent

@Composable
fun SettingsScreenContent(
    items: List<ListItemModel>
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            items = items,
            key = { it.id }
        ) { model ->
            ListItem(model = model)
            Divider()
        }
    }
}

/**
 * Экран, предоставляющий пользователю доступ к различным настройкам приложения.
 * Здесь можно управлять темой оформления, звуками, кодом-доступа и другими параметрами.
 * На данный момент экран является самодостаточным и управляет своим состоянием локально
 * (например, состоянием переключателя темной темы).
 */
@Composable
fun SettingsScreen() {
    var isDarkMode by remember { mutableStateOf(false) }

    val settingsItems = listOf(
        ListItemModel(
            id = "dark_theme",
            type = ItemType.SETTING,
            title = stringResource(id = R.string.dark_theme),
            trailingContent = TrailingContent.Switch(
                isChecked = isDarkMode,
                onToggle = { isDarkMode = it }
            ),
            showTrailingArrow = false
        ),
        ListItemModel(
            id = "primary_color",
            type = ItemType.SETTING,
            title = stringResource(id = R.string.primary_color),
            trailingContent = TrailingContent.ArrowOnly(customIconRes = R.drawable.ic_settings_arrow),
            showTrailingArrow = false
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
            showTrailingArrow = false
        ),
        ListItemModel(
            id = "passcode",
            type = ItemType.SETTING,
            title = stringResource(id = R.string.passcode),
            trailingContent = TrailingContent.ArrowOnly(customIconRes = R.drawable.ic_settings_arrow),
            showTrailingArrow = false
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
            trailingContent = TrailingContent.ArrowOnly(customIconRes = R.drawable.ic_settings_arrow),
            showTrailingArrow = false
        ),
        ListItemModel(
            id = "about",
            type = ItemType.SETTING,
            title = stringResource(id = R.string.about),
            trailingContent = TrailingContent.ArrowOnly(customIconRes = R.drawable.ic_settings_arrow),
            showTrailingArrow = false
        )
    )

    SettingsScreenContent(items = settingsItems)
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}