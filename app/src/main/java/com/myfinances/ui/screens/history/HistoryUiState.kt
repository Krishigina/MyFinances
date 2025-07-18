package com.myfinances.ui.screens.history

import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.ui.model.HistoryUiModel

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Content(
        val uiModel: HistoryUiModel,
        val transactionType: TransactionTypeFilter,
        val parentRoute: String
    ) : HistoryUiState
}