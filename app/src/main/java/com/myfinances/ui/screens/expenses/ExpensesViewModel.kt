package com.myfinances.ui.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.usecase.GetExpenseTransactionsUseCase
import com.myfinances.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val getExpenseTransactionsUseCase: GetExpenseTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExpensesUiState>(ExpensesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = ExpensesUiState.Loading

            when (val result = getExpenseTransactionsUseCase(accountId = 1)) {
                is Result.Success -> {
                    _uiState.value = ExpensesUiState.Success(
                        transactions = result.data.first,
                        categories = result.data.second
                    )
                }

                is Result.Error -> {
                    _uiState.value =
                        ExpensesUiState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }
}