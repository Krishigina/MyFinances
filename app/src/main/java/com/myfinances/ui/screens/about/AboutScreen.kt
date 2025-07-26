package com.myfinances.ui.screens.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
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

@Composable
fun AboutScreen(
    viewModel: AboutViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ListItem(
            model = ListItemModel(
                id = "app_version",
                type = ItemType.SETTING,
                title = stringResource(id = R.string.about_app_version),
                trailingContent = TrailingContent.TextOnly(uiState.appInfo.version),
                showTrailingArrow = false
            )
        )
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))
        ListItem(
            model = ListItemModel(
                id = "build_date",
                type = ItemType.SETTING,
                title = stringResource(id = R.string.about_last_updated),
                trailingContent = TrailingContent.TextOnly(uiState.appInfo.buildDate),
                showTrailingArrow = false
            )
        )
        HorizontalDivider()
    }
}