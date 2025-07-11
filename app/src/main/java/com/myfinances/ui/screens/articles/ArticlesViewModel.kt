package com.myfinances.ui.screens.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.toListItemModel
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

    private var allCategories: List<Pair<String, com.myfinances.ui.components.ListItemModel>> =
        emptyList()

    init {
        loadCategories()
    }

    fun onSearchQueryChanged(newQuery: String) {
        val currentState = _uiState.value
        if (currentState is ArticlesUiState.Success) {
            val filteredItems = if (newQuery.isBlank()) {
                allCategories.map { it.second }
            } else {
                allCategories.filter { it.first.contains(newQuery, ignoreCase = true) }
                    .map { it.second }
            }
            _uiState.update {
                currentState.copy(
                    query = newQuery,
                    categoryItems = filteredItems
                )
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = ArticlesUiState.Loading
            when (val result = getCategoriesUseCase()) {
                is Result.Success -> {
                    allCategories = result.data
                        .filter { !it.isIncome }
                        .map { it.name to it.toListItemModel() }
                        .sortedBy { it.first }

                    _uiState.value = ArticlesUiState.Success(
                        query = "",
                        categoryItems = allCategories.map { it.second }
                    )
                }
                is Result.Error -> _uiState.value =
                    ArticlesUiState.Error(result.exception.message ?: "Unknown error")
                is Result.NetworkError -> _uiState.value = ArticlesUiState.NoInternet
            }
        }
    }
}