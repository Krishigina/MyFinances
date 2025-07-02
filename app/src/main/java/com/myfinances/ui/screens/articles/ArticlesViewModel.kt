package com.myfinances.ui.screens.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.network.ConnectivityManagerSource
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.toListItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана "Статьи".
 * Отвечает за загрузку категорий расходов, преобразование их в UI-модели
 * и управление состоянием экрана.
 */
@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val connectivityManager: ConnectivityManagerSource
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArticlesUiState>(ArticlesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        observeNetworkStatus()
        loadCategories()
    }

    private fun observeNetworkStatus() {
        connectivityManager.isNetworkAvailable
            .onEach { isAvailable ->
                if (isAvailable && _uiState.value is ArticlesUiState.NoInternet) {
                    loadCategories()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = ArticlesUiState.Loading
            when (val result = getCategoriesUseCase()) {
                is Result.Success -> {
                    val expenseCategories = result.data.filter { !it.isIncome }
                    val listItems = expenseCategories.map { it.toListItemModel() }
                    _uiState.value = ArticlesUiState.Success(listItems)
                }

                is Result.Error -> {
                    _uiState.value =
                        ArticlesUiState.Error(result.exception.message ?: "Unknown error")
                }

                is Result.NetworkError -> {
                    _uiState.value = ArticlesUiState.NoInternet
                }
            }
        }
    }
}