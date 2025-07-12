package com.myfinances.ui.mappers

import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction
import com.myfinances.ui.model.TransactionItemUiModel
import com.myfinances.ui.util.formatCurrency
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionDomainToUiMapper {
    private val historyDateTimeFormat = SimpleDateFormat("d MMMM · HH:mm", Locale("ru"))

    fun toSimpleUiModel(
        transaction: Transaction,
        category: Category?,
        currencyCode: String
    ): TransactionItemUiModel {
        return TransactionItemUiModel(
            id = transaction.id.toString(),
            title = category?.name ?: "Неизвестно",
            amountFormatted = formatCurrency(transaction.amount, currencyCode),
            emoji = category?.emoji ?: "❓",
            subtitle = transaction.comment
        )
    }

    fun toHistoryUiModel(
        transaction: Transaction,
        category: Category?,
        currencyCode: String
    ): TransactionItemUiModel {
        return TransactionItemUiModel(
            id = transaction.id.toString(),
            title = category?.name ?: "Неизвестно",
            amountFormatted = formatCurrency(transaction.amount, currencyCode),
            emoji = category?.emoji ?: "❓",
            subtitle = transaction.comment,
            secondaryText = historyDateTimeFormat.format(transaction.date)
        )
    }
}