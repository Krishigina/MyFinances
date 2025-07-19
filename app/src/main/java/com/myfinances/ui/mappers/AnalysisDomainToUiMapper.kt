package com.myfinances.ui.mappers

import androidx.compose.ui.graphics.Color
import com.myfinances.domain.entity.AnalysisData
import com.myfinances.ui.model.AnalysisUiModel
import com.myfinances.ui.model.CategorySpendingUiModel
import com.myfinances.ui.theme.BrightGreen
import com.myfinances.ui.util.formatCurrency
import javax.inject.Inject
import kotlin.math.roundToInt

class AnalysisDomainToUiMapper @Inject constructor() {

    private val chartColors = listOf(
        BrightGreen,
        Color(0xFFFACC15), // yellow
        Color(0xFF22D3EE), // cyan
        Color(0xFFF9A8D4), // pink
        Color(0xFFF87171), // red
        Color(0xFFA78BFA), // violet
        Color(0xFF34D399), // emerald
        Color(0xFFFB923C)  // orange
    )

    fun map(analysisData: AnalysisData): AnalysisUiModel {
        val currencyCode = analysisData.account.currency
        val totalAmount = analysisData.totalAmount

        val categorySpentsUi = analysisData.categorySpents.mapIndexed { index, categorySpending ->
            val percentage = if (totalAmount > 0) {
                ((categorySpending.amount / totalAmount) * 100).roundToInt()
            } else {
                0
            }

            CategorySpendingUiModel(
                id = categorySpending.category.id.toString(),
                title = categorySpending.category.name,
                emoji = categorySpending.category.emoji ?: "❓",
                amountFormatted = formatCurrency(categorySpending.amount, currencyCode),
                percentage = percentage,
                color = chartColors[index % chartColors.size],
                topTransactionId = categorySpending.topTransactionId
            )
        }

        return AnalysisUiModel(
            categorySpents = categorySpentsUi,
            totalAmountFormatted = formatCurrency(totalAmount, currencyCode),
            startDate = analysisData.startDate,
            endDate = analysisData.endDate
        )
    }
}