// app/src/main/java/com/myfinances/ui/screens/haptics/HapticsScreen.kt
package com.myfinances.ui.screens.haptics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myfinances.R
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TrailingContent
import com.myfinances.ui.model.HapticsUiModel

@Composable
fun HapticsScreen(
    viewModel: HapticsScreenViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            ListItem(
                model = ListItemModel(
                    id = "haptics_toggle",
                    type = ItemType.SETTING,
                    title = stringResource(R.string.haptics_enable),
                    trailingContent = TrailingContent.Switch(
                        isChecked = uiState.isEnabled,
                        onToggle = {
                            viewModel.onEvent(HapticsScreenEvent.OnHapticsToggled(it))
                        }
                    ),
                    showTrailingArrow = false
                )
            )
            HorizontalDivider()
        }

        item {
            AnimatedVisibility(visible = uiState.isEnabled) {
                Column {
                    Row(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)) {
                        Text(
                            text = stringResource(R.string.haptic_effect_title),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        items(
            items = uiState.effects,
            key = { it.effect.name }
        ) { model ->
            AnimatedVisibility(visible = uiState.isEnabled) {
                Column {
                    HapticEffectItem(
                        model = model,
                        onClick = { viewModel.onEvent(HapticsScreenEvent.OnEffectSelected(model.effect)) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun HapticEffectItem(
    model: HapticsUiModel,
    onClick: () -> Unit
) {
    ListItem(
        model = ListItemModel(
            id = model.effect.name,
            type = ItemType.SETTING,
            title = model.name,
            trailingContent = if (model.isSelected) {
                TrailingContent.Custom {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.haptic_effect_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else null,
            showTrailingArrow = false,
            onClick = onClick
        )
    )
}