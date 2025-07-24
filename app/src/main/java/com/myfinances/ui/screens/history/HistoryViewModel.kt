package com.myfinances.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.data.manager.SnackbarManager
import com.myfinances.data.manager.SyncUpdateManager
import com.myfinances.domain.entity.TransactionData
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.usecase.GetTransactionsUseCase
import com.myfinances.domain.util.Result
import com.myfinances.domain.util.withTimeAtStartOfDay
import com.myfinances.ui.mappers.TransactionDomainToUiMapper
import com.myfinances.ui.model.HistoryUiModel
import com.myfinances.ui.util.formatSyncTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val accountUpdateManager: AccountUpdateManager,
    private val syncUpdateManager: SyncUpdateManager,
    private val snackbarManager: SnackbarManager,
    private val mapper: TransactionDomainToUiMapper
) : ViewModel() {

    private lateinit var transactionType: TransactionTypeFilter
    private lateinit var parentRoute: String

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var dataCollectionJob: Job? = null

    fun initialize(filter: TransactionTypeFilter, parent: String) {
        if (this::transactionType.isInitialized) return
        this.transactionType = filter
        this.parentRoute = parent

        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = calendar.withTimeAtStartOfDay().time
        loadData(startDate, endDate)

        viewModelScope.launch {
            accountUpdateManager.accountUpdateFlow.collect {
                (_uiState.value as? HistoryUiState.Content)?.let {
                    loadData(it.uiModel.startDate, it.uiModel.endDate)
                }
            }
        }

        viewModelScope.launch {
            syncUpdateManager.syncCompletedFlow.collect { syncTime ->
                snackbarManager.showMessage("Синхронизация завершена: ${formatSyncTime(syncTime)}")
            }
        }
    }

    fun onEvent(event: HistoryEvent) {
        val contentState = (_uiState.value as? HistoryUiState.Content) ?: return

        when (event) {
            is HistoryEvent.StartDateSelected -> {
                val newStartDate = Date(event.timestampMillis)
                if (!newStartDate.after(contentState.uiModel.endDate)) {
                    loadData(newStartDate, contentState.uiModel.endDate)
                }
            }
            is HistoryEvent.EndDateSelected -> {
                val newEndDate = Date(event.timestampMillis)
                if (!newEndDate.before(contentState.uiModel.startDate)) {
                    loadData(contentState.uiModel.startDate, newEndDate)
                }
            }
        }
    }

    private fun loadData(startDate: Date, endDate: Date) {
        dataCollectionJob?.cancel()
        dataCollectionJob = getTransactionsUseCase(startDate, endDate, transactionType)
            .onEach { result ->
                when (result) {
                    is Result.Success -> processSuccess(result.data)
                    is Result.Failure -> {
                        val message = when(result) {
                            is Result.Failure.ApiError -> "Ошибка API: ${result.code}"
                            is Result.Failure.GenericError -> result.exception.message ?: "Неизвестная ошибка"
                            is Result.Failure.NetworkError -> "Ошибка сети. Проверьте подключение."
                        }
                        snackbarManager.showMessage(message)
                        if (_uiState.value is HistoryUiState.Loading) {
                            _uiState.value = createEmptyContentState(startDate, endDate)
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun processSuccess(data: TransactionData) {
        val items = data.transactions.map {
            mapper.toHistoryUiModel(it, data.categories[it.categoryId], data.account.currency)
        }

        val historyUiModel = HistoryUiModel(
            transactionItems = items,
            totalAmount = data.totalAmount,
            currencyCode = data.account.currency,
            startDate = data.startDate,
            endDate = data.endDate
        )

        _uiState.value = HistoryUiState.Content(historyUiModel, transactionType, parentRoute)
    }

    private fun createEmptyContentState(startDate: Date, endDate: Date): HistoryUiState.Content {
        return HistoryUiState.Content(
            HistoryUiModel(
                emptyList(), 0.0, "₽", startDate, endDate
            ),
            transactionType,
            parentRoute
        )
    }
}