package com.myfinances.ui.screens.history

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TrailingContent
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
 * @param onStartDateClick Коллбэк для открытия диалога выбора начальной даты.
 * @param onEndDateClick Коллбэк для открытия диалога выбора конечной даты.
 */
@Composable
fun HistorySummaryBlock(
    startDate: Date,
    endDate: Date,
    totalAmount: Double,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit
) {
    val summaryDateFormat = SimpleDateFormat("d MMMM yyyy", Locale("ru"))

    val summaryItems = listOf(
        ListItemModel(
            id = "history_summary_start",
            title = "Начало",
            type = ItemType.TOTAL,
            trailingContent = TrailingContent.TextOnly(summaryDateFormat.format(startDate)),
            showTrailingArrow = false,
            onClick = onStartDateClick
        ),
        ListItemModel(
            id = "history_summary_end",
            title = "Конец",
            type = ItemType.TOTAL,
            trailingContent = TrailingContent.TextOnly(summaryDateFormat.format(endDate)),
            showTrailingArrow = false,
            onClick = onEndDateClick
        ),
        ListItemModel(
            id = "history_summary_total",
            title = "Сумма",
            type = ItemType.TOTAL,
            trailingContent = TrailingContent.TextOnly(formatCurrency(totalAmount)),
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