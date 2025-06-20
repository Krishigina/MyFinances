package com.myfinances.ui.screens.expenses

import androidx.compose.foundation.layout.Box
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
import com.myfinances.ui.util.formatCurrency

@Composable
fun ExpensesScreen(
    navController: NavHostController,
    viewModel: ExpensesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is ExpensesUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is ExpensesUiState.Success -> {
                ExpensesScreenContent(
                    transactions = state.transactions,
                    categories = state.categories
                )
            }

            is ExpensesUiState.Error -> {
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
private fun ExpensesScreenContent(
    transactions: List<Transaction>,
    categories: List<Category>
) {
    val categoryMap = categories.associateBy { it.id }
    val totalAmount = transactions.sumOf { it.amount }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            val totalAmountItem = ListItemModel(
                id = "total_amount_card_expenses",
                title = stringResource(id = R.string.total_amount_card),
                type = ItemType.TOTAL,
                trailingContent = TrailingContent.TextOnly(formatCurrency(totalAmount)),
                showTrailingArrow = false
            )
            ListItem(model = totalAmountItem)
            Divider()
        }

        items(items = transactions, key = { it.id }) { transaction ->
            val category = categoryMap[transaction.categoryId]
            val model = transaction.toListItemModel(
                categoryName = category?.name ?: stringResource(id = R.string.unknown),
                emoji = category?.emoji ?: "❓"
            )
            ListItem(model = model)
            Divider()
        }
    }
}

private fun Transaction.toListItemModel(categoryName: String, emoji: String): ListItemModel {
    return ListItemModel(
        id = this.id.toString(),
        title = categoryName,
        type = ItemType.TRANSACTION,
        leadingIcon = LeadingIcon.Emoji(emoji),
        subtitle = this.comment,
        trailingContent = TrailingContent.TextWithArrow(
            text = formatCurrency(this.amount)
        ),
        showTrailingArrow = true
    )
}