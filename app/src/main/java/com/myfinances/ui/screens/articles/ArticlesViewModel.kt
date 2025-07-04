package com.myfinances.ui.screens.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.entity.Category
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.toListItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана "Статьи".
 *
 * Отвечает за:
 * - Загрузку полного списка категорий с помощью [GetCategoriesUseCase].
 * - Реализацию логики локального поиска по названию категории.
 * - Управление состоянием UI через [ArticlesUiState].
 */
@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArticlesUiState>(ArticlesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    private var allCategories: List<Category> = emptyList()

    init {
        loadCategories()
        observeSearchQuery()
    }

    /**
     * Обновляет поисковый запрос при вводе текста пользователем.
     * @param query Новый текст из поля поиска.
     */
    fun onSearchQueryChanged(query: String) {
        _uiState.update { currentState ->
            if (currentState is ArticlesUiState.Success) {
                currentState.copy(query = query)
            } else {
                currentState
            }
        }
        searchQuery.value = query
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = ArticlesUiState.Loading
            when (val result = getCategoriesUseCase()) {
                is Result.Success -> {
                    allCategories = result.data
                    _uiState.value = ArticlesUiState.Success(
                        query = "",
                        categoryItems = allCategories.map { it.toListItemModel() }
                    )
                }

                is Result.Error -> _uiState.value =
                    ArticlesUiState.Error(result.exception.message ?: "Unknown error")

                is Result.NetworkError -> _uiState.value = ArticlesUiState.NoInternet
            }
        }
    }

    private fun observeSearchQuery() {
        searchQuery
            .debounce(300L)
            .onEach { query ->
                val filteredList = if (query.isBlank()) {
                    allCategories
                } else {
                    allCategories.filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                }
                _uiState.update { currentState ->
                    if (currentState is ArticlesUiState.Success) {
                        currentState.copy(categoryItems = filteredList.map { it.toListItemModel() })
                    } else {
                        currentState
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}