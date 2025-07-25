package com.myfinances.ui.mappers

import com.myfinances.domain.entity.AnalysisData
import com.myfinances.ui.model.AnalysisUiModel
import com.myfinances.ui.model.CategorySpendingUiModel
import com.myfinances.ui.util.formatCurrency
import javax.inject.Inject
import kotlin.math.roundToInt

class AnalysisDomainToUiMapper @Inject constructor() {

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