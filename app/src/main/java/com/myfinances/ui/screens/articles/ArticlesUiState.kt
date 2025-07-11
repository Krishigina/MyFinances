package com.myfinances.ui.screens.articles

import com.myfinances.ui.model.ArticlesUiModel

sealed interface ArticlesUiState {
    data object Loading : ArticlesUiState
    data class Success(
        val query: String,
        val articlesModel: ArticlesUiModel
    ) : ArticlesUiState
    data class Error(val message: String) : ArticlesUiState
    data object NoInternet : ArticlesUiState
}