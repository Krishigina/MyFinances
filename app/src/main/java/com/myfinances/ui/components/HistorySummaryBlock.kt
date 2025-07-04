package com.myfinances.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.myfinances.R
import com.myfinances.ui.util.formatCurrency
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Компонент, отображающий блок с информацией о выбранном периоде и итоговой сумме.
 *
 * @param startDate Начальная дата периода.
 * @param endDate Конечная дата периода.
 * @param totalAmount Общая сумма транзакций за период.
 * @param currencyCode Код валюты для форматирования итоговой суммы.
 * @param onStartDateClick Коллбэк для открытия диалога выбора начальной даты.
 * @param onEndDateClick Коллбэк для открытия диалога выбора конечной даты.
 */
@Composable
fun HistorySummaryBlock(
    startDate: Date,
    endDate: Date,
    totalAmount: Double,
    currencyCode: String,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit
) {
    val summaryDateFormat = SimpleDateFormat("d MMMM yyyy", Locale("ru"))

    val summaryItems = listOf(
        ListItemModel(
            id = "history_summary_start",
            title = stringResource(id = R.string.period_start_date),
            type = ItemType.TOTAL,
            trailingContent = TrailingContent.TextOnly(summaryDateFormat.format(startDate)),
            showTrailingArrow = false,
            onClick = onStartDateClick
        ),
        ListItemModel(
            id = "history_summary_end",
            title = stringResource(id = R.string.period_end_date),
            type = ItemType.TOTAL,
            trailingContent = TrailingContent.TextOnly(summaryDateFormat.format(endDate)),
            showTrailingArrow = false,
            onClick = onEndDateClick
        ),
        ListItemModel(
            id = "history_summary_total",
            title = stringResource(id = R.string.period_total_amount),
            type = ItemType.TOTAL,
            trailingContent = TrailingContent.TextOnly(formatCurrency(totalAmount, currencyCode)),
            showTrailingArrow = false
        )
    )

    Column {
        summaryItems.forEach { model ->
            ListItem(model = model)
            HorizontalDivider()
        }
    }
}