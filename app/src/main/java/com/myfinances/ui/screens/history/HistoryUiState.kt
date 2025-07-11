package com.myfinances.ui.screens.history

import com.myfinances.ui.model.HistoryUiModel

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Content(val uiModel: HistoryUiModel) : HistoryUiState
}