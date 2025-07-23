package com.myfinances.ui.screens.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.data.manager.SyncUpdateManager
import com.myfinances.domain.entity.TransactionData
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.TransactionDomainToUiMapper
import com.myfinances.ui.model.TransactionItemUiModel
import com.myfinances.ui.util.formatCurrency
import com.myfinances.ui.util.formatSyncTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class BaseTransactionsViewModel<T, E : UiEvent>(
    private val accountUpdateManager: AccountUpdateManager,
    private val syncUpdateManager: SyncUpdateManager,
    private val mapper: TransactionDomainToUiMapper
) : ViewModel() {

    protected val _uiState = MutableStateFlow<T>(getInitialState())
    val uiState = _uiState.asStateFlow()

    val snackbarHostState = SnackbarHostState()
    private var dataCollectionJob: Job? = null

    protected fun startDataCollection() {
        collectData()

        viewModelScope.launch {
            accountUpdateManager.accountUpdateFlow.collect {
                collectData()
            }
        }

        viewModelScope.launch {
            syncUpdateManager.syncCompletedFlow.collect { syncTime ->
                showInfo("Синхронизация завершена: ${formatSyncTime(syncTime)}")
            }
        }
    }

    private fun collectData() {
        dataCollectionJob?.cancel()
        dataCollectionJob = getDataFlow()
            .onEach { result ->
                when (result) {
                    is Result.Success -> processSuccess(result.data)
                    is Result.Failure.GenericError -> {
                        showError(result.exception.message ?: "Неизвестная ошибка")
                    }
                    else -> { }
                }
            }
            .launchIn(viewModelScope)

        refreshData()
    }

    private fun refreshData() {
        viewModelScope.launch {
            if (!isContentState(_uiState.value)) {
                _uiState.value = getLoadingState()
            }

            when (val refreshResult = refreshDataUseCase()) {
                is Result.Success -> { }
                is Result.Failure -> {
                    val message = when (refreshResult) {
                        is Result.Failure.ApiError -> "Ошибка API: ${refreshResult.message}"
                        is Result.Failure.GenericError -> refreshResult.exception.message ?: "Ошибка обновления"
                        is Result.Failure.NetworkError -> "Нет подключения к сети. Отображаются последние данные."
                    }
                    showInfo(message)
                }
            }
        }
    }

    private fun processSuccess(data: TransactionData) {
        val items = data.transactions.map {
            mapper.toSimpleUiModel(it, data.categories[it.categoryId], data.account.currency)
        }
        val totalAmountFormatted = formatCurrency(data.totalAmount, data.account.currency)
        _uiState.value = createContentState(items, totalAmountFormatted)

        if (items.isEmpty() && isContentState(_uiState.value)) {
            showInfo(getEmptyDataMessage())
        }
    }

    private fun showError(message: String) {
        viewModelScope.launch { snackbarHostState.showSnackbar(message = message) }
        if (!isContentState(_uiState.value)) {
            _uiState.value = createContentState(emptyList(), formatCurrency(0.0, "RUB"))
        }
    }

    private fun showInfo(message: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
        }
    }
    protected abstract fun getInitialState(): T
    protected abstract fun getLoadingState(): T
    protected abstract fun isContentState(state: T): Boolean
    protected abstract fun createContentState(items: List<TransactionItemUiModel>, total: String): T
    protected abstract fun getEmptyDataMessage(): String
    protected abstract fun getDataFlow(): Flow<Result<TransactionData>>
    protected abstract suspend fun refreshDataUseCase(): Result<Unit>
}