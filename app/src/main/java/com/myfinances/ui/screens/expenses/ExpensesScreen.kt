package com.myfinances.ui.screens.expenses

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.myfinances.R
import com.myfinances.data.MockData
import com.myfinances.domain.entity.Transaction
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.LeadingIcon
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TrailingContent
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ExpensesScreenContent(
    transactions: List<Transaction>
) {
    val totalAmount = transactions.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }

    val totalAmountItem = ListItemModel(
        id = "total_amount_card",
        title = stringResource(id = R.string.total_amount_card),
        type = ItemType.TOTAL,
        leadingIcon = null,
        trailingContent = TrailingContent.TextOnly(formatCurrency(totalAmount)),
        onClick = {}
    )

    val transactionListItems = transactions.map { transaction ->
        val category = MockData.findCategoryById(transaction.categoryId)
        transaction.toListItemModel(
            categoryName = category?.name ?: stringResource(id = R.string.unknown),
            emoji = category?.emoji ?: "❓",
            type = ItemType.TRANSACTION
        )
    }

    val allItems = listOf(totalAmountItem) + transactionListItems

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            items = allItems,
            key = { it.id }
        ) { model ->
            ListItem(model = model)
            Divider()
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    format.maximumFractionDigits = 0
    return format.format(amount).replace(" ", "\u00A0")
}

private fun Transaction.toListItemModel(
    categoryName: String,
    emoji: String,
    type: ItemType
): ListItemModel {
    return ListItemModel(
        id = this.id.toString(),
        title = categoryName,
        type = type,
        leadingIcon = LeadingIcon.Emoji(emoji),
        subtitle = this.comment,
        trailingContent = TrailingContent.TextWithArrow(formatCurrency(this.amount.toDouble())),
        onClick = { }
    )
}

@Composable
fun ExpensesScreen() {
    val expenseTransactions = MockData.transactions.filter {
        MockData.findCategoryById(it.categoryId)?.isIncome == false
    }
    ExpensesScreenContent(transactions = expenseTransactions)
}

@Preview(showBackground = true)
@Composable
fun ExpensesScreenPreview() {
    ExpensesScreen()
}