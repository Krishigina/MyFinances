package com.myfinances.ui.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.usecase.GetTransactionsUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.screens.common.TransactionsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionsUiState>(TransactionsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = TransactionsUiState.Loading

            val categoriesDeferred = async { getCategoriesUseCase() }
            val transactionsDeferred = async {
                val calendar = Calendar.getInstance()
                val endDate = calendar.time
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startDate = calendar.time
                getTransactionsUseCase(accountId = 1, startDate, endDate)
            }

            val categoriesResult = categoriesDeferred.await()
            val transactionsResult = transactionsDeferred.await()

            when {
                categoriesResult is Result.Error -> _uiState.value =
                    TransactionsUiState.Error(categoriesResult.exception.message ?: "Error")

                transactionsResult is Result.Error -> _uiState.value =
                    TransactionsUiState.Error(transactionsResult.exception.message ?: "Error")

                categoriesResult is Result.Success && transactionsResult is Result.Success -> {
                    val categories = categoriesResult.data
                    val allTransactions = transactionsResult.data
                    val categoryMap = categories.associateBy { it.id }

                    val expenseTransactions =
                        allTransactions.filter { categoryMap[it.categoryId]?.isIncome == false }

                    _uiState.value = TransactionsUiState.Success(
                        transactions = expenseTransactions.sortedByDescending { it.date },
                        categories = categories
                    )
                }
            }
        }
    }
}