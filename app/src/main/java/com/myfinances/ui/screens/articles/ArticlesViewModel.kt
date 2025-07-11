package com.myfinances.ui.screens.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.entity.Category
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.CategoryDomainToUiMapper
import com.myfinances.ui.model.ArticlesUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ArticlesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val mapper: CategoryDomainToUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArticlesUiState>(ArticlesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var originalCategories: List<Category> = emptyList()

    init {
        loadCategories()
    }

    fun onSearchQueryChanged(newQuery: String) {
        val currentState = _uiState.value
        if (currentState !is ArticlesUiState.Success) return

        val filteredItems = if (newQuery.isBlank()) {
            originalCategories
        } else {
            originalCategories.filter {
                it.name.contains(newQuery, ignoreCase = true)
            }
        }.map { mapper.mapToUiModel(it) }

        _uiState.update {
            currentState.copy(
                query = newQuery,
                articlesModel = ArticlesUiModel(categoryItems = filteredItems)
            )
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = ArticlesUiState.Loading
            when (val result = getCategoriesUseCase()) {
                is Result.Success -> {
                    originalCategories = result.data
                    val uiItems = result.data.map { mapper.mapToUiModel(it) }
                    _uiState.value = ArticlesUiState.Success(
                        query = "",
                        articlesModel = ArticlesUiModel(categoryItems = uiItems)
                    )
                }
                is Result.Error -> _uiState.value =
                    ArticlesUiState.Error(result.exception.message ?: "Unknown error")
                is Result.NetworkError -> _uiState.value = ArticlesUiState.NoInternet
            }
        }
    }
}