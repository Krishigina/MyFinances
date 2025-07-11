package com.myfinances.ui.mappers

import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.LeadingIcon
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TrailingContent
import com.myfinances.ui.util.formatCurrency
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class TransactionDomainToUiMapper @Inject constructor() {
    private val historyDateTimeFormat = SimpleDateFormat("d MMMM · HH:mm", Locale("ru"))

    fun toSimpleListItemModel(
        transaction: Transaction,
        category: Category?,
        currencyCode: String
    ): ListItemModel {
        return ListItemModel(
            id = transaction.id.toString(),
            title = category?.name ?: "Неизвестно",
            type = ItemType.TRANSACTION,
            leadingIcon = LeadingIcon.Emoji(category?.emoji ?: "❓"),
            subtitle = transaction.comment,
            trailingContent = TrailingContent.TextWithArrow(
                text = formatCurrency(transaction.amount, currencyCode)
            ),
            showTrailingArrow = true,
            onClick = {} // TODO: Add navigation to transaction details
        )
    }

    fun toHistoryListItemModel(
        transaction: Transaction,
        category: Category?,
        currencyCode: String
    ): ListItemModel {
        return ListItemModel(
            id = transaction.id.toString(),
            title = category?.name ?: "Неизвестно",
            type = ItemType.TRANSACTION,
            leadingIcon = LeadingIcon.Emoji(category?.emoji ?: "❓"),
            subtitle = transaction.comment,
            trailingContent = TrailingContent.TextWithArrow(
                text = formatCurrency(transaction.amount, currencyCode),
                secondaryText = historyDateTimeFormat.format(transaction.date)
            ),
            showTrailingArrow = true,
            onClick = {} // TODO: Add navigation to transaction details
        )
    }
}