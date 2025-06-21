package com.myfinances.ui.screens.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.myfinances.R
import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.LeadingIcon
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TrailingContent
import com.myfinances.ui.theme.BrightBlack
import com.myfinances.ui.theme.BrightGreen
import com.myfinances.ui.theme.PastelGreen
import com.myfinances.ui.util.formatCurrency
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun greenDatePickerColors(): DatePickerColors = DatePickerDefaults.colors(
    containerColor = PastelGreen,
    selectedDayContainerColor = BrightGreen,
    selectedDayContentColor = BrightBlack,
    todayDateBorderColor = Color.Transparent,
    todayContentColor = BrightBlack,
    dayContentColor = BrightBlack,
    weekdayContentColor = BrightBlack,
    subheadContentColor = BrightBlack,
    yearContentColor = BrightBlack,
    currentYearContentColor = BrightGreen,
    selectedYearContainerColor = BrightGreen,
    selectedYearContentColor = BrightBlack
)

@Composable
private fun DatePickerDialogButtons(
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onClear,
            colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = BrightBlack)
        ) {
            Text("Clear")
        }

        Spacer(modifier = Modifier.weight(1f))

        Row {
            TextButton(
                onClick = onDismiss,
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = BrightBlack)
            ) {
                Text("Cancel")
            }
            TextButton(
                onClick = onConfirm,
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = BrightBlack)
            ) {
                Text("OK")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is HistoryUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is HistoryUiState.Success -> {
                HistoryScreenContent(
                    transactions = state.transactions,
                    categories = state.categories,
                    startDate = state.startDate,
                    endDate = state.endDate,
                    onStartDateClick = { showStartDatePicker = true },
                    onEndDateClick = { showEndDatePicker = true }
                )
            }
            is HistoryUiState.Error -> {
                Text(
                    text = state.message,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
            is HistoryUiState.NoInternet -> {
                Text(
                    text = "Нет подключения к интернету. Проверьте соединение и попробуйте снова.",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        @Composable
        fun CustomDatePickerDialog(
            datePickerState: DatePickerState,
            onDismiss: () -> Unit,
            onConfirm: (Long?) -> Unit
        ) {
            DatePickerDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    DatePickerDialogButtons(
                        onDismiss = onDismiss,
                        onClear = { datePickerState.selectedDateMillis = null },
                        onConfirm = {
                            onConfirm(datePickerState.selectedDateMillis)
                            onDismiss()
                        }
                    )
                },
                dismissButton = null,
                colors = greenDatePickerColors()
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = greenDatePickerColors(),
                    title = null,
                    headline = null,
                    showModeToggle = false
                )
            }
        }

        if (showStartDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = (uiState as? HistoryUiState.Success)?.startDate?.time
            )
            CustomDatePickerDialog(
                datePickerState = datePickerState,
                onDismiss = { showStartDatePicker = false },
                onConfirm = { timestamp ->
                    timestamp?.let { viewModel.onEvent(HistoryEvent.StartDateSelected(it)) }
                }
            )
        }

        if (showEndDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = (uiState as? HistoryUiState.Success)?.endDate?.time
            )
            CustomDatePickerDialog(
                datePickerState = datePickerState,
                onDismiss = { showEndDatePicker = false },
                onConfirm = { timestamp ->
                    timestamp?.let { viewModel.onEvent(HistoryEvent.EndDateSelected(it)) }
                }
            )
        }
    }
}

@Composable
private fun HistoryScreenContent(
    transactions: List<Transaction>,
    categories: List<Category>,
    startDate: Date,
    endDate: Date,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit
) {
    val categoryMap = categories.associateBy { it.id }
    val totalSum = transactions.sumOf { it.amount }
    val summaryDateFormat = SimpleDateFormat("d MMMM yyyy", Locale("ru"))
    val itemDateTimeFormat = SimpleDateFormat("d MMMM · HH:mm", Locale("ru"))

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            val summaryItems = listOf(
                ListItemModel(
                    id = "history_summary_start",
                    title = "Начало",
                    type = ItemType.TOTAL,
                    trailingContent = TrailingContent.TextOnly(summaryDateFormat.format(startDate)),
                    showTrailingArrow = false,
                    onClick = onStartDateClick
                ),
                ListItemModel(
                    id = "history_summary_end",
                    title = "Конец",
                    type = ItemType.TOTAL,
                    trailingContent = TrailingContent.TextOnly(summaryDateFormat.format(endDate)),
                    showTrailingArrow = false,
                    onClick = onEndDateClick
                ),
                ListItemModel(
                    id = "history_summary_total",
                    title = "Сумма",
                    type = ItemType.TOTAL,
                    trailingContent = TrailingContent.TextOnly(formatCurrency(totalSum)),
                    showTrailingArrow = false
                )
            )
            Column {
                summaryItems.forEach { model ->
                    ListItem(model = model)
                    Divider()
                }
            }
        }

        items(items = transactions, key = { it.id }) { transaction ->
            val category = categoryMap[transaction.categoryId]
            val model = transaction.toListItemModel(
                categoryName = category?.name ?: stringResource(id = R.string.unknown),
                emoji = category?.emoji ?: "❓",
                formattedDateTime = itemDateTimeFormat.format(transaction.date)
            )
            ListItem(model = model)
            Divider()
        }
    }
}

private fun Transaction.toListItemModel(
    categoryName: String,
    emoji: String,
    formattedDateTime: String
): ListItemModel {
    return ListItemModel(
        id = this.id.toString(),
        title = categoryName,
        type = ItemType.TRANSACTION,
        leadingIcon = LeadingIcon.Emoji(emoji),
        subtitle = this.comment,
        trailingContent = TrailingContent.TextWithArrow(
            text = formatCurrency(this.amount),
            secondaryText = formattedDateTime
        ),
        showTrailingArrow = true
    )
}