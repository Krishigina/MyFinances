package com.myfinances.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.myfinances.R
import com.myfinances.ui.components.CurrencyPickerBottomSheet
import com.myfinances.ui.components.EditableListItem
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.LeadingIcon
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TrailingContent
import com.myfinances.ui.util.getCurrencySymbol

@Composable
fun AccountScreen(
    navController: NavHostController,
    viewModel: AccountViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is AccountUiState.Loading -> CircularProgressIndicator()
            is AccountUiState.Success -> {
                val background = if (state.isEditMode) {
                    MaterialTheme.colorScheme.background
                } else {
                    Color(0xFFF3EDF7)
                }
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(background)) {
                    if (state.isEditMode) {
                        AccountEditContent(state = state, onEvent = viewModel::onEvent)
                    } else {
                        AccountViewContent(state = state, onEvent = viewModel::onEvent)
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountViewContent(
    state: AccountUiState.Success,
    onEvent: (AccountEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.medium
            )
            .clip(MaterialTheme.shapes.medium)
    ) {
        ListItem(
            model = ListItemModel(
                id = "balance_view",
                title = stringResource(R.string.balance),
                type = ItemType.TOTAL,
                leadingIcon = LeadingIcon.Emoji(state.account.emoji),
                useWhiteIconBackground = true,
                trailingContent = TrailingContent.TextWithArrow(
                    text = state.account.balanceFormatted
                ),
                onClick = { onEvent(AccountEvent.EditModeToggled) },
                showTrailingArrow = true
            )
        )
        Divider()
        ListItem(
            model = ListItemModel(
                id = "currency_view",
                title = stringResource(R.string.currency),
                type = ItemType.TOTAL,
                trailingContent = TrailingContent.TextWithArrow(text = state.account.currencySymbol),
                onClick = { onEvent(AccountEvent.EditModeToggled) },
                showTrailingArrow = true
            )
        )
    }
}

@Composable
private fun AccountEditContent(
    state: AccountUiState.Success,
    onEvent: (AccountEvent) -> Unit
) {
    if (state.showCurrencyPicker) {
        CurrencyPickerBottomSheet(
            availableCurrencies = state.availableCurrencies,
            onCurrencySelected = { onEvent(AccountEvent.CurrencySelected(it.code)) },
            onDismiss = { onEvent(AccountEvent.CurrencyPickerToggled) }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        EditableListItem(
            model = ListItemModel(
                id = "edit_name",
                title = "",
                type = ItemType.SETTING,
                leadingIcon = LeadingIcon.Resource(R.drawable.ic_account),
            ),
            value = state.draftName,
            onValueChange = { onEvent(AccountEvent.NameChanged(it)) },
            placeholder = stringResource(R.string.account_name_placeholder)
        )
        Divider()

        EditableListItem(
            model = ListItemModel(
                id = "edit_balance",
                title = stringResource(R.string.balance),
                type = ItemType.SETTING
            ),
            value = state.draftBalance,
            onValueChange = { onEvent(AccountEvent.BalanceChanged(it)) },
            keyboardType = KeyboardType.Decimal,
            textAlign = TextAlign.End
        )
        Divider()

        ListItem(
            model = ListItemModel(
                id = "edit_currency",
                title = stringResource(R.string.currency),
                type = ItemType.SETTING,
                trailingContent = TrailingContent.TextWithArrow(
                    text = getCurrencySymbol(state.draftCurrency)
                ),
                onClick = { onEvent(AccountEvent.CurrencyPickerToggled) }
            )
        )
        Divider()
    }
}