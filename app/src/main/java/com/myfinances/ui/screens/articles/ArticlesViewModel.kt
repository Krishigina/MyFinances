package com.myfinances.ui.screens.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.manager.SnackbarManager
import com.myfinances.domain.entity.Category
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.CategoryDomainToUiMapper
import com.myfinances.ui.model.ArticlesUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ArticlesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val snackbarManager: SnackbarManager,
    private val mapper: CategoryDomainToUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArticlesUiState>(ArticlesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var originalCategories: List<Category> = emptyList()
    private var dataCollectionJob: Job? = null

    init {
        observeCategories()
        refreshCategories()
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

    private fun observeCategories() {
        dataCollectionJob?.cancel()
        dataCollectionJob = getCategoriesUseCase().onEach { categories ->
            originalCategories = categories
            val uiItems = categories.map { mapper.mapToUiModel(it) }

            val currentState = _uiState.value
            if (currentState is ArticlesUiState.Success) {
                _uiState.update {
                    currentState.copy(articlesModel = ArticlesUiModel(uiItems))
                }
            } else {
                _uiState.value = ArticlesUiState.Success(
                    query = "",
                    articlesModel = ArticlesUiModel(uiItems)
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun refreshCategories() {
        viewModelScope.launch {
            when (val refreshResult = getCategoriesUseCase.refresh()) {
                is Result.Success -> {
                }
                is Result.Failure -> {
                    val message = when (refreshResult) {
                        is Result.Failure.ApiError -> "Ошибка сервера: ${refreshResult.code}"
                        is Result.Failure.GenericError -> refreshResult.exception.message ?: "Не удалось загрузить категории"
                        is Result.Failure.NetworkError -> "Нет сети, показаны сохраненные данные"
                    }
                    snackbarManager.showMessage(message)
                }
            }
        }
    }
}