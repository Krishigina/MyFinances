package com.myfinances.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.usecase.GetTransactionsUseCase
import com.myfinances.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Success(
        val transactions: List<Transaction>,
        val categories: List<Category>,
        val startDate: Date,
        val endDate: Date
    ) : HistoryUiState

    data class Error(val message: String) : HistoryUiState
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading

            val categoriesDeferred = async { getCategoriesUseCase() }

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val endDate = calendar.time

            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val startDate = calendar.time

            val transactionsDeferred =
                async { getTransactionsUseCase(accountId = 1, startDate, endDate) }

            val categoriesResult = categoriesDeferred.await()
            val transactionsResult = transactionsDeferred.await()

            when {
                categoriesResult is Result.Error -> {
                    _uiState.value = HistoryUiState.Error(
                        categoriesResult.exception.message ?: "Failed to load categories"
                    )
                }

                transactionsResult is Result.Error -> {
                    _uiState.value = HistoryUiState.Error(
                        transactionsResult.exception.message ?: "Failed to load transactions"
                    )
                }

                categoriesResult is Result.Success && transactionsResult is Result.Success -> {
                    _uiState.value = HistoryUiState.Success(
                        transactions = transactionsResult.data.sortedByDescending { it.date },
                        categories = categoriesResult.data,
                        startDate = startDate,
                        endDate = endDate
                    )
                }
            }
        }
    }
}