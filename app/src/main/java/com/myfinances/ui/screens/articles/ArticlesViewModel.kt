package com.myfinances.ui.screens.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.entity.Category
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.toListItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана "Статьи".
 *
 * Отвечает за:
 * - Загрузку полного списка категорий расходов с помощью [GetCategoriesUseCase].
 * - Реализацию логики локального поиска по названию категории.
 * - Управление состоянием UI через [ArticlesUiState].
 *
 * Для реализации поиска используется комбинация нескольких [StateFlow] и операторов `combine` и `debounce`.
 * `allCategories` хранит полный, нефильтрованный список.
 * `searchQuery` хранит текущий текст из поля поиска.
 * `combine` реагирует на изменения в любом из этих потоков и создает отфильтрованный список.
 * `debounce` используется для того, чтобы не выполнять фильтрацию на каждое нажатие клавиши.
 */
@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArticlesUiState>(ArticlesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")
    private val allCategories = MutableStateFlow<List<Category>>(emptyList())

    init {
        loadCategories()
        observeSearchQuery()
    }

    /**
     * Обновляет поисковый запрос при вводе текста пользователем.
     * @param query Новый текст из поля поиска.
     */
    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = ArticlesUiState.Loading
            when (val result = getCategoriesUseCase()) {
                is Result.Success -> {
                    val expenseCategories = result.data.filter { !it.isIncome }
                    allCategories.value = expenseCategories
                    _uiState.value = ArticlesUiState.Success(
                        query = "",
                        categoryItems = expenseCategories.map { it.toListItemModel() }
                    )
                }

                is Result.Error -> _uiState.value =
                    ArticlesUiState.Error(result.exception.message ?: "Unknown error")

                is Result.NetworkError -> _uiState.value = ArticlesUiState.NoInternet
            }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQuery
                .debounce(300L)
                .combine(allCategories) { query, categories ->
                    val filteredList = if (query.isBlank()) {
                        categories
                    } else {
                        categories.filter {
                            it.name.contains(query, ignoreCase = true)
                        }
                    }
                    if (_uiState.value is ArticlesUiState.Success) {
                        _uiState.value = ArticlesUiState.Success(
                            query = query,
                            categoryItems = filteredList.map { it.toListItemModel() }
                        )
                    }
                }
                .collect()
        }
    }
}