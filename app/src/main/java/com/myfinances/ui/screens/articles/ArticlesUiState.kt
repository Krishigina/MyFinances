package com.myfinances.ui.screens.articles

import com.myfinances.ui.components.ListItemModel

sealed interface ArticlesUiState {
    data object Loading : ArticlesUiState
    data class Success(val categoryItems: List<ListItemModel>) : ArticlesUiState
    data class Error(val message: String) : ArticlesUiState
    data object NoInternet : ArticlesUiState
}