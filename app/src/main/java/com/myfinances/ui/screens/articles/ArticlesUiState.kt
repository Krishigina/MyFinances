package com.myfinances.ui.screens.articles

import com.myfinances.ui.components.ListItemModel

/**
 * Определяет все возможные состояния UI для экрана "Статьи".
 */
sealed interface ArticlesUiState {
    data object Loading : ArticlesUiState
    data class Success(
        val query: String,
        val categoryItems: List<ListItemModel>
    ) : ArticlesUiState
    data class Error(val message: String) : ArticlesUiState
    data object NoInternet : ArticlesUiState
}