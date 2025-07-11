package com.myfinances.ui.screens.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.domain.entity.TransactionData
import com.myfinances.domain.util.Result
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.mappers.TransactionDomainToUiMapper
import com.myfinances.ui.util.formatCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseTransactionsViewModel<T>(
    private val accountUpdateManager: AccountUpdateManager,
    private val mapper: TransactionDomainToUiMapper
) : ViewModel() {

    protected val _uiState = MutableStateFlow<T>(getInitialLoadingState())
    val uiState = _uiState.asStateFlow()

    val snackbarHostState = SnackbarHostState()

    init {
        viewModelScope.launch {
            accountUpdateManager.accountUpdateFlow.collect {
                loadData()
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            if (!isContentState(_uiState.value)) {
                _uiState.value = getInitialLoadingState()
            }

            when (val result = getTransactionsUseCase()) {
                is Result.Success -> processSuccess(result.data)
                is Result.Error -> showError(result.exception.message ?: "Неизвестная ошибка")
                is Result.NetworkError -> showError("Ошибка сети. Проверьте подключение.")
            }
        }
    }

    private fun processSuccess(data: TransactionData) {
        val items = data.transactions.map {
            mapper.toSimpleListItemModel(it, data.categories[it.categoryId], data.account.currency)
        }
        _uiState.value =
            createContentState(items, formatCurrency(data.totalAmount, data.account.currency))

        if (items.isEmpty()) {
            showInfo(getEmptyDataMessage())
        }
    }

    private fun showError(message: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(message = message)
        }
        if (!isContentState(_uiState.value)) {
            _uiState.value = createContentState(emptyList(), "0.00")
        }
    }

    private fun showInfo(message: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    protected abstract suspend fun getTransactionsUseCase(): Result<TransactionData>
    protected abstract fun getInitialLoadingState(): T
    protected abstract fun isContentState(state: T): Boolean
    protected abstract fun createContentState(items: List<ListItemModel>, total: String): T
    protected abstract fun getEmptyDataMessage(): String
}