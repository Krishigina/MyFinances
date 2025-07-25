package com.myfinances.ui.screens.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.data.manager.SnackbarManager
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
    private val snackbarManager: SnackbarManager,
    private val mapper: TransactionDomainToUiMapper
) : ViewModel() {

    protected val _uiState = MutableStateFlow<T>(getInitialState())
    val uiState = _uiState.asStateFlow()

    private var dataCollectionJob: Job? = null
    private var emptyMessageShown = false

    abstract fun onEvent(event: E)

    protected fun startDataCollection() {
        collectDataFromDb()

        viewModelScope.launch {
            accountUpdateManager.accountUpdateFlow.collect {
                emptyMessageShown = false
                collectDataFromDb()

            }
        }

        viewModelScope.launch {
            syncUpdateManager.syncCompletedFlow.collect { syncTime ->
                snackbarManager.showMessage("Синхронизация завершена: ${formatSyncTime(syncTime)}")
            }
        }
    }

    private fun collectDataFromDb() {
        dataCollectionJob?.cancel()
        dataCollectionJob = getDataFlow()
            .onEach { result ->
                when (result) {
                    is Result.Success -> processSuccess(result.data)
                    is Result.Failure.GenericError -> {
                        snackbarManager.showMessage(result.exception.message ?: "Неизвестная ошибка")
                        if (!isContentState(_uiState.value)) {
                            _uiState.value = createContentState(emptyList(), "")
                        }
                    }
                    else -> { }
                }
            }
            .launchIn(viewModelScope)
    }

    protected fun refreshData(showLoading: Boolean) {
        viewModelScope.launch {
            if (showLoading) {
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
                    snackbarManager.showMessage(message)
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

        if (items.isEmpty() && !emptyMessageShown) {
            snackbarManager.showMessage(getEmptyDataMessage())
            emptyMessageShown = true
        } else if (items.isNotEmpty()) {
            emptyMessageShown = false
        }
    }

    abstract fun getEmptyDataMessage(): String
    protected abstract fun getInitialState(): T
    protected abstract fun getLoadingState(): T
    protected abstract fun isContentState(state: T): Boolean
    protected abstract fun createContentState(items: List<TransactionItemUiModel>, total: String): T
    protected abstract fun getDataFlow(): Flow<Result<TransactionData>>
    protected abstract suspend fun refreshDataUseCase(): Result<Unit>
}