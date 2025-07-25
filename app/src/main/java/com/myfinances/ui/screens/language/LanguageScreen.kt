package com.myfinances.ui.screens.language

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myfinances.R
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TrailingContent
import com.myfinances.ui.model.LanguageUiModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LanguageScreen(
    viewModel: LanguageScreenViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()

    LaunchedEffect(key1 = viewModel.sideEffect) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is LanguageScreenSideEffect.RecreateActivity -> {
                    activity?.let {
                        it.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        it.recreate()
                    }
                }
            }
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            items = uiState.languages,
            key = { it.language.name }
        ) { model ->
            LanguageItem(
                model = model,
                onClick = { viewModel.onEvent(LanguageScreenEvent.OnLanguageSelected(model.language)) }
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun LanguageItem(
    model: LanguageUiModel,
    onClick: () -> Unit
) {
    ListItem(
        model = ListItemModel(
            id = model.language.name,
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

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}