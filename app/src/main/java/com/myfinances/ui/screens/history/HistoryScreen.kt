package com.myfinances.ui.screens.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.myfinances.ui.util.formatCurrency
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Главный Composable-компонент экрана "История".
 * Отвечает за отображение состояния (загрузка, ошибка, успех) и управление
 * видимостью диалоговых окон выбора даты.
 */
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
                    text = stringResource(id = R.string.no_internet_connection),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        if (showStartDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = (uiState as? HistoryUiState.Success)?.startDate?.time
            )
            HistoryDatePickerDialog(
                datePickerState = datePickerState,
                onDismissRequest = { showStartDatePicker = false },
                onConfirm = { timestamp ->
                    timestamp?.let { viewModel.onEvent(HistoryEvent.StartDateSelected(it)) }
                }
            )
        }

        if (showEndDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = (uiState as? HistoryUiState.Success)?.endDate?.time
            )
            HistoryDatePickerDialog(
                datePickerState = datePickerState,
                onDismissRequest = { showEndDatePicker = false },
                onConfirm = { timestamp ->
                    timestamp?.let { viewModel.onEvent(HistoryEvent.EndDateSelected(it)) }
                }
            )
        }
    }
}

/**
 * Компонент, отвечающий за отрисовку контента экрана "История" при успешной загрузке данных.
 */
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
    val itemDateTimeFormat = SimpleDateFormat("d MMMM · HH:mm", Locale("ru"))

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            HistorySummaryBlock(
                startDate = startDate,
                endDate = endDate,
                totalAmount = totalSum,
                onStartDateClick = onStartDateClick,
                onEndDateClick = onEndDateClick
            )
        }

        items(items = transactions, key = { it.id }) { transaction ->
            val category = categoryMap[transaction.categoryId]
            val model = transaction.toListItemModel(
                categoryName = category?.name ?: stringResource(id = R.string.unknown),
                emoji = category?.emoji ?: "❓",
                formattedDateTime = itemDateTimeFormat.format(transaction.date)
            )
            ListItem(model = model)
            HorizontalDivider()
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