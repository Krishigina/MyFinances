package com.myfinances.ui.screens.analysis

import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.ui.model.AnalysisUiModel

sealed interface AnalysisUiState {
    data object Loading : AnalysisUiState
    data class Content(
        val uiModel: AnalysisUiModel,
        val transactionType: TransactionTypeFilter,
        val parentRoute: String
    ) : AnalysisUiState
    data class Error(val message: String) : AnalysisUiState
}