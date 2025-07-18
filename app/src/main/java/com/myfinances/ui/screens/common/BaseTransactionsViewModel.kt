package com.myfinances.ui.screens.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.domain.entity.TransactionData
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.TransactionDomainToUiMapper
import com.myfinances.ui.model.TransactionItemUiModel
import com.myfinances.ui.util.formatCurrency
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class BaseTransactionsViewModel<T, E : UiEvent>(
    private val accountUpdateManager: AccountUpdateManager,
    private val mapper: TransactionDomainToUiMapper
) : ViewModel() {

    protected val _uiState = MutableStateFlow<T>(getInitialState())
    val uiState = _uiState.asStateFlow()

    val snackbarHostState = SnackbarHostState()
    private var dataCollectionJob: Job? = null

    init {
        // При инициализации ViewModel, сразу начинаем слушать данные
        collectData()

        // Также слушаем глобальные обновления счета
        viewModelScope.launch {
            accountUpdateManager.accountUpdateFlow.collect {
                // При обновлении счета, просто перезапускаем сбор данных.
                // Это также вызовет refresh.
                collectData()
            }
        }
    }

    open fun onEvent(event: E) {
        when (event) {
            is CommonEvent.Refresh -> refreshData()
        }
    }

    private fun collectData() {
        dataCollectionJob?.cancel() // Отменяем предыдущую подписку
        dataCollectionJob = getDataFlow()
            .onEach { result ->
                when (result) {
                    is Result.Success -> processSuccess(result.data)
                    is Result.Error -> {
                        // Ошибки от Flow (например, не найден счет)
                        showError(result.exception.message ?: "Неизвестная ошибка")
                    }
                    // NetworkError не обрабатывается здесь, так как Flow читает из базы
                    is Result.NetworkError -> { /* Ignore */ }
                }
            }
            .launchIn(viewModelScope)

        // При первой подписке или принудительном обновлении - запрашиваем данные из сети
        refreshData()
    }

    private fun refreshData() {
        viewModelScope.launch {
            // Показываем индикатор загрузки, только если сейчас не контент
            if (!isContentState(_uiState.value)) {
                _uiState.value = getLoadingState()
            }

            // Вызываем suspend функцию refresh из use case
            when (val refreshResult = refreshDataUseCase()) {
                is Result.Error -> showError(refreshResult.exception.message ?: "Ошибка обновления")
                is Result.NetworkError -> showInfo("Нет подключения к сети. Отображаются последние данные.")
                is Result.Success -> { /* Данные обновятся через Flow */ }
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
        // Если была ошибка, переводим в состояние контента с пустыми данными,
        // чтобы пользователь видел пустой экран, а не вечную загрузку.
        _uiState.value = createContentState(emptyList(), formatCurrency(0.0, "RUB"))
    }

    private fun showInfo(message: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
        }
    }

    // Абстрактные методы, которые должны реализовать наследники
    protected abstract fun getInitialState(): T
    protected abstract fun getLoadingState(): T
    protected abstract fun isContentState(state: T): Boolean
    protected abstract fun createContentState(items: List<TransactionItemUiModel>, total: String): T
    protected abstract fun getEmptyDataMessage(): String

    // Абстрактные методы для связи с UseCases
    protected abstract fun getDataFlow(): Flow<Result<TransactionData>>
    protected abstract suspend fun refreshDataUseCase(): Result<Unit>
}