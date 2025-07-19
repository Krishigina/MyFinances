package com.myfinances.ui.screens.history

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.ui.components.HistoryDatePickerDialog
import com.myfinances.ui.components.HistorySummaryBlock
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.LeadingIcon
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TrailingContent
import com.myfinances.ui.model.HistoryUiModel
import com.myfinances.ui.navigation.Destination
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val contentState = uiState as? HistoryUiState.Content

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is HistoryUiState.Loading -> {
                CircularProgressIndicator()
            }

            is HistoryUiState.Content -> {
                HistoryScreenContent(
                    navController = navController,
                    transactionType = state.transactionType,
                    parentRoute = state.parentRoute,
                    uiModel = state.uiModel,
                    onStartDateClick = { showStartDatePicker = true },
                    onEndDateClick = { showEndDatePicker = true }
                )
            }
        }

        if (showStartDatePicker && contentState != null) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = contentState.uiModel.startDate.time,
                yearRange = (2020..Calendar.getInstance().get(Calendar.YEAR)),
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis <= contentState.uiModel.endDate.time
                    }
                }
            )
            HistoryDatePickerDialog(
                datePickerState = datePickerState,
                onDismissRequest = { showStartDatePicker = false },
                onConfirm = { timestamp ->
                    timestamp?.let { viewModel.onEvent(HistoryEvent.StartDateSelected(it)) }
                }
            )
        }

        if (showEndDatePicker && contentState != null) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = contentState.uiModel.endDate.time,
                yearRange = (2020..Calendar.getInstance().get(Calendar.YEAR)),
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis >= contentState.uiModel.startDate.time
                    }
                }
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

@Composable
private fun HistoryScreenContent(
    navController: NavController,
    transactionType: TransactionTypeFilter,
    parentRoute: String,
    uiModel: HistoryUiModel,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            HistorySummaryBlock(
                startDate = uiModel.startDate,
                endDate = uiModel.endDate,
                totalAmount = uiModel.totalAmount,
                currencyCode = uiModel.currencyCode,
                onStartDateClick = onStartDateClick,
                onEndDateClick = onEndDateClick
            )
        }

        items(items = uiModel.transactionItems, key = { it.id }) { model ->
            ListItem(
                model = ListItemModel(
                    id = model.id,
                    title = model.title,
                    subtitle = model.subtitle,
                    type = ItemType.TRANSACTION,
                    leadingIcon = LeadingIcon.Emoji(model.emoji),
                    trailingContent = TrailingContent.TextWithArrow(
                        text = model.amountFormatted,
                        secondaryText = model.secondaryText
                    ),
                    showTrailingArrow = true,
                    onClick = {
                        val route = Destination.AddEditTransaction.createRoute(
                            transactionType = transactionType,
                            transactionId = model.id.toInt(),
                            parentRoute = parentRoute
                        )
                        Log.d("DEBUG_NAV", "[HistoryScreen] Navigating to route: $route")
                        navController.navigate(route)
                    }
                )
            )
            HorizontalDivider()
        }
    }
}