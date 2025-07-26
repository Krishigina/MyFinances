package com.myfinances.ui.screens.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.myfinances.R
import com.myfinances.ui.components.DonutChart
import com.myfinances.ui.components.ErrorMessage
import com.myfinances.ui.components.HistoryDatePickerDialog
import com.myfinances.ui.components.LeadingIcon
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TrailingContent
import com.myfinances.ui.model.AnalysisUiModel
import com.myfinances.ui.navigation.Destination
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    navController: NavController,
    viewModel: AnalysisViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is AnalysisUiState.Loading -> {
                CircularProgressIndicator()
            }
            is AnalysisUiState.Content -> {
                AnalysisScreenContent(
                    state = state,
                    navController = navController,
                    onStartDateClick = { showStartDatePicker = true },
                    onEndDateClick = { showEndDatePicker = true }
                )
            }
            is AnalysisUiState.Error -> {
                ErrorMessage(message = state.message)
            }
        }

        val contentState = uiState as? AnalysisUiState.Content
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
                    timestamp?.let { viewModel.onEvent(AnalysisEvent.StartDateSelected(it)) }
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
                    timestamp?.let { viewModel.onEvent(AnalysisEvent.EndDateSelected(it)) }
                }
            )
        }
    }
}

@Composable
private fun AnalysisScreenContent(
    state: AnalysisUiState.Content,
    navController: NavController,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit
) {
    val uiModel = state.uiModel
    val dateFormat = remember { SimpleDateFormat("MMMM yyyy", Locale("ru")) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                PeriodRow(
                    label = stringResource(R.string.period_start_date),
                    date = dateFormat.format(uiModel.startDate).replaceFirstChar { it.uppercase() },
                    onClick = onStartDateClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider()
                PeriodRow(
                    label = stringResource(R.string.period_end_date),
                    date = dateFormat.format(uiModel.endDate).replaceFirstChar { it.uppercase() },
                    onClick = onEndDateClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.period_total_amount),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = uiModel.totalAmountFormatted,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            HorizontalDivider()
        }

        if (uiModel.categorySpents.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DonutChart(
                        modifier = Modifier.size(width = 200.dp, height = 194.dp),
                        items = uiModel.categorySpents
                    )
                }
                HorizontalDivider()
            }
        }

        items(items = uiModel.categorySpents, key = { it.id }) { model ->
            Column {
                ListItem(
                    model = ListItemModel(
                        id = model.id,
                        title = model.title,
                        subtitle = null,
                        type = com.myfinances.ui.components.ItemType.TRANSACTION,
                        leadingIcon = LeadingIcon.Emoji(model.emoji),
                        trailingContent = TrailingContent.TextWithArrow(
                            text = "${model.percentage}%",
                            secondaryText = model.amountFormatted
                        ),
                        showTrailingArrow = true,
                        onClick = {
                            model.topTransactionId?.let { transactionId ->
                                val route = Destination.AddEditTransaction.createRoute(
                                    transactionType = state.transactionType,
                                    transactionId = transactionId,
                                    parentRoute = state.parentRoute
                                )
                                navController.navigate(route)
                            }
                        }
                    )
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun PeriodRow(
    label: String,
    date: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100))
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}