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

/**
 * Преобразует доменную сущность [Category] в UI-модель [ListItemModel]
 * для отображения на экране "Статьи".
 *
 * @return [ListItemModel] готовый для отображения в списке.
 */
fun Category.toListItemModel(): ListItemModel {
    return ListItemModel(
        id = this.id.toString(),
        type = ItemType.TRANSACTION,
        leadingIcon = this.emoji?.let { LeadingIcon.Emoji(it) },
        title = this.name,
        trailingContent = null,
        showTrailingArrow = false
    )
}

/**
 * Преобразует доменную сущность [Transaction] в UI-модель [ListItemModel]
 * для отображения на экранах "Расходы" и "Доходы".
 *
 * @param category Соответствующая категория для транзакции, чтобы получить ее имя и иконку.
 * @param currencyCode Код валюты для корректного форматирования суммы.
 * @return [ListItemModel] готовый для отображения в списке.
 */
fun Transaction.toSimpleListItemModel(
    category: Category?,
    currencyCode: String
): ListItemModel {
    return ListItemModel(
        id = this.id.toString(),
        title = category?.name ?: "Неизвестно",
        type = ItemType.TRANSACTION,
        leadingIcon = LeadingIcon.Emoji(category?.emoji ?: "❓"),
        subtitle = this.comment,
        trailingContent = TrailingContent.TextWithArrow(
            text = formatCurrency(this.amount, currencyCode)
        ),
        showTrailingArrow = true,
        onClick = {}
    )
}

/**
 * Преобразует доменную сущность [Transaction] в UI-модель [ListItemModel]
 * для отображения на экране "История". Отличается от [toSimpleListItemModel]
 * наличием даты и времени в `secondaryText`.
 *
 * @param category Соответствующая категория для транзакции, чтобы получить ее имя и иконку.
 * @param currencyCode Код валюты для корректного форматирования суммы.
 * @return [ListItemModel] готовый для отображения в списке.
 */
fun Transaction.toHistoryListItemModel(
    category: Category?,
    currencyCode: String
): ListItemModel {
    val itemDateTimeFormat = SimpleDateFormat("d MMMM · HH:mm", Locale("ru"))

    return ListItemModel(
        id = this.id.toString(),
        title = category?.name ?: "Неизвестно",
        type = ItemType.TRANSACTION,
        leadingIcon = LeadingIcon.Emoji(category?.emoji ?: "❓"),
        subtitle = this.comment,
        trailingContent = TrailingContent.TextWithArrow(
            text = formatCurrency(this.amount, currencyCode),
            secondaryText = itemDateTimeFormat.format(this.date)
        ),
        showTrailingArrow = true,
        onClick = {}
    )
}