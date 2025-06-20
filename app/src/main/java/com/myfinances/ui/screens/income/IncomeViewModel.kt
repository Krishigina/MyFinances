package com.myfinances.ui.screens.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.usecase.GetIncomeTransactionsUseCase
import com.myfinances.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val getIncomeTransactionsUseCase: GetIncomeTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<IncomeUiState>(IncomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = IncomeUiState.Loading

            when (val result = getIncomeTransactionsUseCase(accountId = 1)) {
                is Result.Success -> {
                    _uiState.value = IncomeUiState.Success(
                        transactions = result.data.first,
                        categories = result.data.second
                    )
                }

                is Result.Error -> {
                    _uiState.value =
                        IncomeUiState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }
}