package com.myfinances.ui.screens.articles

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.entity.Category
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.CategoryDomainToUiMapper
import com.myfinances.ui.model.ArticlesUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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

            val refreshResult = getCategoriesUseCase.refresh()
            val categoriesFromDb = getCategoriesUseCase().first()

            if (categoriesFromDb.isNotEmpty()) {
                originalCategories = categoriesFromDb
                val uiItems = categoriesFromDb.map { mapper.mapToUiModel(it) }
                _uiState.value = ArticlesUiState.Success(
                    query = "",
                    articlesModel = ArticlesUiModel(categoryItems = uiItems)
                )
                if (refreshResult is Result.Failure.NetworkError) {
                    Log.w("ArticlesViewModel", "Network error during refresh, showing cached data.")
                }
            } else {
                when (refreshResult) {
                    is Result.Success -> {
                        originalCategories = emptyList()
                        _uiState.value = ArticlesUiState.Success(
                            query = "",
                            articlesModel = ArticlesUiModel(emptyList())
                        )
                    }
                    is Result.Failure.NetworkError -> _uiState.value = ArticlesUiState.NoInternet
                    is Result.Failure.ApiError -> _uiState.value = ArticlesUiState.Error("Ошибка сервера: ${refreshResult.code}")
                    is Result.Failure.GenericError -> _uiState.value = ArticlesUiState.Error(refreshResult.exception.message ?: "Не удалось загрузить категории")
                }
            }
        }
    }
}