package com.myfinances.ui.screens.income

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
fun IncomeScreenContent(
    transactions: List<Transaction>
) {
    val totalAmount = transactions.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }

    val totalAmountItem = ListItemModel(
        id = "total_amount_card",
        title = "Всего",
        type = ItemType.TOTAL,
        leadingIcon = null,
        trailingContent = TrailingContent.TextOnly(formatCurrency(totalAmount)),
        onClick = {}
    )

    val transactionListItems = transactions.map { transaction ->
        val category = MockData.findCategoryById(transaction.categoryId)
        transaction.toListItemModel(
            categoryName = category?.name ?: "Неизвестно",
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
fun IncomeScreen() {
    val incomeTransactions = MockData.transactions.filter {
        MockData.findCategoryById(it.categoryId)?.isIncome == true
    }
    IncomeScreenContent(transactions = incomeTransactions)
}

@Preview(showBackground = true)
@Composable
fun IncomeScreenPreview() {
    IncomeScreen()
}