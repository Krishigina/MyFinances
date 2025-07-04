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
import com.myfinances.R
import com.myfinances.ui.components.HistoryDatePickerDialog
import com.myfinances.ui.components.HistorySummaryBlock
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
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
                    transactionItems = state.transactionItems,
                    totalAmount = state.totalAmount,
                    startDate = state.startDate,
                    endDate = state.endDate,
                    onStartDateClick = { showStartDatePicker = true },
                    onEndDateClick = { showEndDatePicker = true },
                    state = state
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

@Composable
private fun HistoryScreenContent(
    transactionItems: List<ListItemModel>,
    totalAmount: Double,
    startDate: Date,
    endDate: Date,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    state: HistoryUiState.Success
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            HistorySummaryBlock(
                startDate = startDate,
                endDate = endDate,
                totalAmount = totalAmount,
                currencyCode = state.currency,
                onStartDateClick = onStartDateClick,
                onEndDateClick = onEndDateClick
            )
        }

        items(items = transactionItems, key = { it.id }) { model ->
            ListItem(model = model)
            HorizontalDivider()
        }
    }
}