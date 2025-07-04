package com.myfinances.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
//import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myfinances.R
import com.myfinances.ui.components.CurrencyModel
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.LeadingIcon
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TrailingContent
import com.myfinances.ui.theme.LocalDimensions
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
                    trailingContent = TrailingContent.TextWithArrow(
                        text = formatCurrency(
                            state.account.balance, state.account.currency
                        )
                    ),
                    onClick = { onEvent(AccountEvent.EditModeToggled) },
                    showTrailingArrow = true
                )
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
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
                leadingIcon = LeadingIcon.Vector(Icons.Default.AccountCircle),
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
                type = ItemType.SETTING,
                leadingIcon = LeadingIcon.Vector(Icons.Default.AccountCircle)
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

/**
 * Универсальный Composable, который рендерит ListItem, но делает его поля редактируемыми.
 */
@Composable
private fun EditableListItem(
    model: ListItemModel,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    textAlign: TextAlign = TextAlign.Start
) {
    val dimensions = LocalDimensions.current
    val textStyle = MaterialTheme.typography.bodyLarge

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensions.listItem.heightTotal)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = dimensions.spacing.paddingLarge),
        verticalAlignment = Alignment.CenterVertically
    ) {
        model.leadingIcon?.let { icon ->
            val iconBackgroundColor =
                if (model.useWhiteIconBackground) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.secondary
            Box(
                modifier = Modifier
                    .size(dimensions.icon.medium)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                when (icon) {
                    is LeadingIcon.Emoji -> Text(icon.char, fontSize = 16.sp)
                    is LeadingIcon.Resource -> Icon(
                        painterResource(icon.id),
                        null,
                        modifier = Modifier.size(dimensions.spacing.paddingLarge)
                    )

                    is LeadingIcon.Vector -> Icon(
                        imageVector = icon.imageVector,
                        null,
                        modifier = Modifier.size(dimensions.icon.medium)
                    )
                }
            }
            Spacer(Modifier.width(dimensions.spacing.paddingLarge))
        }

        if (model.title.isNotEmpty()) {
            Text(text = model.title, style = textStyle)
            Spacer(Modifier.width(dimensions.spacing.paddingLarge))
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            textStyle = textStyle.copy(
                textAlign = textAlign,
                color = MaterialTheme.colorScheme.onBackground
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box(contentAlignment = if (textAlign == TextAlign.End) Alignment.CenterEnd else Alignment.CenterStart) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(placeholder, color = Color.Gray, style = textStyle)
                    }
                    innerTextField()
                }
            }
        )
    }
}


/**
 * Модальное окно (BottomSheet) для выбора валюты.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyPickerBottomSheet(
    availableCurrencies: List<CurrencyModel>,
    onCurrencySelected: (CurrencyModel) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            items(availableCurrencies) { currency ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCurrencySelected(currency) }
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = currency.symbol, fontSize = 20.sp, modifier = Modifier.width(40.dp))
                    Text(
                        text = currency.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = currency.symbol, fontSize = 20.sp)
                }
            }
            item {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEEEEE))
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.action_cancel), color = Color.Red)
                }
            }
        }
    }
}

/**
 * Простой компонент для отображения сообщения об ошибке.
 */
@Composable
private fun ErrorMessage(message: String, modifier: Modifier = Modifier) {
    Text(
        text = message,
        modifier = modifier.padding(16.dp),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.error
    )
}