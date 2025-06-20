package com.myfinances.ui.screens.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                    endDate = state.endDate
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
        }
    }
}

@Composable
private fun HistoryScreenContent(
    transactions: List<Transaction>,
    categories: List<Category>,
    startDate: Date,
    endDate: Date
) {
    val categoryMap = categories.associateBy { it.id }
    val totalSum = transactions.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("ru"))
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            val summaryItems = listOf(
                ListItemModel(
                    id = "history_summary_start",
                    title = "Начало",
                    type = ItemType.TOTAL,
                    trailingContent = TrailingContent.TextOnly(dateFormat.format(startDate)),
                    showTrailingArrow = false
                ),
                ListItemModel(
                    id = "history_summary_end",
                    title = "Конец",
                    type = ItemType.TOTAL,
                    trailingContent = TrailingContent.TextOnly(dateFormat.format(endDate)),
                    showTrailingArrow = false
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
                time = timeFormat.format(transaction.date)
            )
            ListItem(model = model)
            Divider()
        }
    }
}

private fun Transaction.toListItemModel(
    categoryName: String,
    emoji: String,
    time: String
): ListItemModel {
    return ListItemModel(
        id = this.id.toString(),
        title = categoryName,
        type = ItemType.TRANSACTION,
        leadingIcon = LeadingIcon.Emoji(emoji),
        subtitle = this.comment,
        trailingContent = TrailingContent.TextWithArrow(
            text = formatCurrency(this.amount.toDouble()),
            secondaryText = time
        ),
        showTrailingArrow = true
    )
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    format.maximumFractionDigits = 0
    return format.format(amount).replace(" ", "\u00A0")
}