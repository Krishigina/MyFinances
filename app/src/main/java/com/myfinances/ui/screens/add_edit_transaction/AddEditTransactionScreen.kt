package com.myfinances.ui.screens.add_edit_transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.myfinances.R
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.ui.components.CategoryPickerBottomSheet
import com.myfinances.ui.components.ErrorDialog
import com.myfinances.ui.components.HistoryDatePickerDialog
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TimePickerDialog
import com.myfinances.ui.components.TrailingContent
import com.myfinances.ui.theme.LocalDimensions
import com.myfinances.ui.util.getCurrencySymbol
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    navController: NavController,
    viewModel: AddEditTransactionViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        val successState = uiState as? AddEditTransactionUiState.Success
        if (successState?.closeScreen == true) {
            navController.popBackStack()
        }
    }

    val state = uiState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is AddEditTransactionUiState.Loading -> CircularProgressIndicator()
            is AddEditTransactionUiState.Success -> {
                HandleDialogs(uiState = state, onEvent = viewModel::onEvent)
                TransactionDetailsContent(uiState = state, onEvent = viewModel::onEvent)
            }
        }
    }
}

@Composable
private fun TransactionDetailsContent(
    uiState: AddEditTransactionUiState.Success,
    onEvent: (AddEditTransactionEvent) -> Unit
) {
    val dimensions = LocalDimensions.current
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            ListItem(
                model = ListItemModel(
                    id = "account",
                    title = stringResource(R.string.account),
                    type = ItemType.SETTING,
                    showTrailingArrow = false,
                    onClick = null,
                    trailingContent = TrailingContent.TextOnly(
                        text = uiState.account?.name ?: "..."
                    )
                )
            )
        }
        item {
            ListItem(
                model = ListItemModel(
                    id = "category",
                    title = stringResource(R.string.category),
                    type = ItemType.SETTING,
                    onClick = { onEvent(AddEditTransactionEvent.ToggleCategoryPicker) },
                    trailingContent = TrailingContent.TextWithArrow(
                        text = uiState.selectedCategory?.name ?: ""
                    )
                )
            )
        }
        item {
            AmountItem(
                amount = uiState.amount,
                currency = uiState.account?.currency,
                onAmountChange = { onEvent(AddEditTransactionEvent.AmountChanged(it)) }
            )
        }
        item {
            ListItem(
                model = ListItemModel(
                    id = "date",
                    title = stringResource(R.string.date),
                    type = ItemType.SETTING,
                    onClick = { onEvent(AddEditTransactionEvent.ToggleDatePicker) },
                    trailingContent = TrailingContent.TextWithArrow(
                        text = dateFormat.format(uiState.date)
                    )
                )
            )
        }
        item {
            ListItem(
                model = ListItemModel(
                    id = "time",
                    title = stringResource(R.string.time),
                    type = ItemType.SETTING,
                    onClick = { onEvent(AddEditTransactionEvent.ToggleTimePicker) },
                    trailingContent = TrailingContent.TextWithArrow(
                        text = timeFormat.format(uiState.date)
                    )
                )
            )
        }
        item {
            CommentItem(
                comment = uiState.comment,
                onCommentChange = { onEvent(AddEditTransactionEvent.CommentChanged(it)) }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = dimensions.spacing.paddingLarge))
        }

        if (uiState.isEditMode) {
            item {
                Spacer(modifier = Modifier.padding(top = 16.dp))
                DeleteButton(
                    transactionType = uiState.transactionType,
                    onClick = { onEvent(AddEditTransactionEvent.ShowDeleteConfirmation) }
                )
            }
        }
    }
}


@Composable
private fun AmountItem(
    amount: String,
    currency: String?,
    onAmountChange: (String) -> Unit
) {
    val dimensions = LocalDimensions.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensions.listItem.heightTotal)
            .padding(horizontal = dimensions.spacing.paddingLarge),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.amount),
            style = MaterialTheme.typography.bodyLarge
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = amount,
                onValueChange = onAmountChange,
                modifier = Modifier.width(140.dp),
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.End
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = getCurrencySymbol(currency ?: "RUB"),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = dimensions.spacing.paddingLarge))
}


@Composable
private fun CommentItem(comment: String, onCommentChange: (String) -> Unit) {
    val dimensions = LocalDimensions.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensions.listItem.heightTotal)
            .padding(horizontal = dimensions.spacing.paddingLarge),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = comment,
            onValueChange = onCommentChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                if (comment.isEmpty()) {
                    Text(
                        text = stringResource(R.string.enter_comment),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                innerTextField()
            }
        )
    }
}

@Composable
private fun DeleteButton(transactionType: TransactionTypeFilter, onClick: () -> Unit) {
    val textRes = if (transactionType == TransactionTypeFilter.INCOME) {
        R.string.delete_income_button
    } else {
        R.string.delete_expense_button
    }
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE46962),
            contentColor = Color.White
        )
    ) {
        Text(
            text = stringResource(textRes),
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HandleDialogs(
    uiState: AddEditTransactionUiState.Success,
    onEvent: (AddEditTransactionEvent) -> Unit
) {
    val calendar = remember(uiState.date) {
        Calendar.getInstance().apply { time = uiState.date }
    }

    if (uiState.showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.date.time,
            yearRange = (2020..Calendar.getInstance().get(Calendar.YEAR))
        )
        HistoryDatePickerDialog(
            datePickerState = datePickerState,
            onDismissRequest = { onEvent(AddEditTransactionEvent.ToggleDatePicker) },
            onConfirm = { timestamp ->
                timestamp?.let {
                    onEvent(AddEditTransactionEvent.DateSelected(Date(it)))
                }
                onEvent(AddEditTransactionEvent.ToggleDatePicker)
            }
        )
    }

    if (uiState.showTimePicker) {
        TimePickerDialog(
            onDismiss = { onEvent(AddEditTransactionEvent.ToggleTimePicker) },
            onConfirm = { hour, minute ->
                onEvent(AddEditTransactionEvent.TimeChanged(hour, minute))
                onEvent(AddEditTransactionEvent.ToggleTimePicker)
            },
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE)
        )
    }

    if (uiState.showCategoryPicker) {
        CategoryPickerBottomSheet(
            categories = uiState.categories,
            onCategorySelected = { onEvent(AddEditTransactionEvent.CategorySelected(it)) },
            onDismiss = { onEvent(AddEditTransactionEvent.ToggleCategoryPicker) },
        )
    }

    uiState.error?.let { errorState ->
        ErrorDialog(
            title = stringResource(R.string.error_dialog_title),
            message = errorState.message,
            onConfirm = {
                errorState.onRetry()
                onEvent(AddEditTransactionEvent.DismissErrorDialog)
            },
            onDismiss = { onEvent(AddEditTransactionEvent.DismissErrorDialog) }
        )
    }

    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { onEvent(AddEditTransactionEvent.DismissDeleteConfirmation) },
            title = { Text(stringResource(R.string.delete_confirmation_title)) },
            text = { Text(stringResource(R.string.delete_confirmation_message)) },
            confirmButton = {
                TextButton(
                    onClick = { onEvent(AddEditTransactionEvent.DeleteTransaction) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(id = R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(AddEditTransactionEvent.DismissDeleteConfirmation) }) {
                    Text(stringResource(id = R.string.action_cancel))
                }
            }
        )
    }
}