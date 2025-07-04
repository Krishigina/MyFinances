package com.myfinances.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myfinances.R
import com.myfinances.ui.components.CurrencyPickerBottomSheet
import com.myfinances.ui.components.EditableListItem
import com.myfinances.ui.components.ErrorMessage
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.LeadingIcon
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TrailingContent
import com.myfinances.ui.util.formatCurrency

/**
 * Главный Composable для экрана "Счет".
 *
 * Этот экран отображает информацию о счете пользователя и позволяет
 * редактировать ее.
 */
@Composable
fun AccountScreen(
    viewModel: AccountViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if ((uiState as? AccountUiState.Success)?.isEditMode == true) MaterialTheme.colorScheme.background
                else
                    Color(0xFFF3EDF7)
            )
    ) {
        when (val state = uiState) {
            is AccountUiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
            is AccountUiState.Success -> {
                if (state.isEditMode) {
                    AccountEditContent(state = state, onEvent = viewModel::onEvent)
                } else {
                    AccountViewContent(state = state, onEvent = viewModel::onEvent)
                }
            }

            is AccountUiState.Error -> ErrorMessage(
                message = state.message,
                modifier = Modifier.align(Alignment.Center)
            )

            is AccountUiState.NoInternet -> ErrorMessage(
                message = stringResource(id = R.string.no_internet_connection),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/**
 * UI-компонент для режима ПРОСМОТРА счета.
 */
@Composable
private fun AccountViewContent(
    state: AccountUiState.Success,
    onEvent: (AccountEvent) -> Unit
) {
    val currencyMap = remember(state.availableCurrencies) {
        state.availableCurrencies.associateBy { it.code }
    }
    val currencySymbol = currencyMap[state.account.currency]?.symbol ?: state.account.currency

    Column(modifier = Modifier.fillMaxSize()) {
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
                        text = formatCurrency(
                            state.account.balance, state.account.currency
                        )
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
                    trailingContent = TrailingContent.TextWithArrow(text = currencySymbol),
                    onClick = { onEvent(AccountEvent.EditModeToggled) },
                    showTrailingArrow = true
                )
            )
        }
    }
}

/**
 * UI-компонент для режима РЕДАКТИРОВАНИЯ счета
 */
@Composable
private fun AccountEditContent(
    state: AccountUiState.Success,
    onEvent: (AccountEvent) -> Unit
) {
    val currencyMap = remember(state.availableCurrencies) {
        state.availableCurrencies.associateBy { it.code }
    }

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
                    text = currencyMap[state.draftCurrency]?.symbol ?: state.draftCurrency
                ),
                onClick = { onEvent(AccountEvent.CurrencyPickerToggled) }
            )
        )
        Divider()

        if (state.isSaving) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
        }

        state.saveError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}