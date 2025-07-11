package com.myfinances.ui.model

import com.myfinances.ui.components.ListItemModel

/**
 * UI-модель, представляющая готовые данные для экранов с транзакциями (Расходы, Доходы).
 * Содержит уже отформатированную итоговую сумму и готовый список элементов для LazyColumn.
 */
data class TransactionSummaryUiModel(
    val transactionItems: List<ListItemModel>,
    val totalAmountFormatted: String
)