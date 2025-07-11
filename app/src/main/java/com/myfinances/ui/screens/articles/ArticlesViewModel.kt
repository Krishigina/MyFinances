package com.myfinances.ui.screens.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.model.ArticlesUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ArticlesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArticlesUiState>(ArticlesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var originalArticles: ArticlesUiModel? = null

    init {
        loadCategories()
    }

    fun onSearchQueryChanged(newQuery: String) {
        val currentArticles = originalArticles ?: return
        val currentState = _uiState.value
        if (currentState !is ArticlesUiState.Success) return

        val filteredItems = if (newQuery.isBlank()) {
            currentArticles.categoryItems
        } else {
            currentArticles.categoryItems.filter {
                it.title.contains(newQuery, ignoreCase = true)
            }
        }
        _uiState.update {
            currentState.copy(
                query = newQuery,
                categoryItems = filteredItems
            )
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = ArticlesUiState.Loading
            when (val result = getCategoriesUseCase()) {
                is Result.Success -> {
                    originalArticles = result.data
                    _uiState.value = ArticlesUiState.Success(
                        query = "",
                        categoryItems = result.data.categoryItems
                    )
                }
                is Result.Error -> _uiState.value =
                    ArticlesUiState.Error(result.exception.message ?: "Unknown error")
                is Result.NetworkError -> _uiState.value = ArticlesUiState.NoInternet
            }
        }
    }
}