package com.myfinances.ui.screens.analysis

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.domain.entity.AnalysisData
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.usecase.GetAnalysisDataUseCase
import com.myfinances.domain.util.Result
import com.myfinances.domain.util.withTimeAtStartOfDay
import com.myfinances.ui.mappers.AnalysisDomainToUiMapper
import com.myfinances.ui.model.AnalysisUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class AnalysisViewModel @Inject constructor(
    private val getAnalysisDataUseCase: GetAnalysisDataUseCase,
    private val accountUpdateManager: AccountUpdateManager,
    private val mapper: AnalysisDomainToUiMapper
) : ViewModel() {

    private lateinit var transactionType: TransactionTypeFilter
    private lateinit var parentRoute: String

    private val _uiState = MutableStateFlow<AnalysisUiState>(AnalysisUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val snackbarHostState = SnackbarHostState()
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
                (_uiState.value as? AnalysisUiState.Content)?.let {
                    loadData(it.uiModel.startDate, it.uiModel.endDate)
                }
            }
        }
    }

    fun onEvent(event: AnalysisEvent) {
        val contentState = (_uiState.value as? AnalysisUiState.Content) ?: return

        when (event) {
            is AnalysisEvent.StartDateSelected -> {
                val newStartDate = Date(event.timestampMillis)
                if (!newStartDate.after(contentState.uiModel.endDate)) {
                    loadData(newStartDate, contentState.uiModel.endDate)
                }
            }
            is AnalysisEvent.EndDateSelected -> {
                val newEndDate = Date(event.timestampMillis)
                if (!newEndDate.before(contentState.uiModel.startDate)) {
                    loadData(contentState.uiModel.startDate, newEndDate)
                }
            }
        }
    }

    private fun loadData(startDate: Date, endDate: Date) {
        dataCollectionJob?.cancel()

        viewModelScope.launch {
            _uiState.value = AnalysisUiState.Loading
            when (val refreshResult = getAnalysisDataUseCase.refresh(startDate, endDate)) {
                is Result.Error -> showError(refreshResult.exception.message ?: "Ошибка обновления")
                is Result.NetworkError -> showInfo("Нет подключения к сети. Отображаются последние данные.")
                is Result.Success -> { }
            }
        }

        dataCollectionJob = getAnalysisDataUseCase(startDate, endDate, transactionType)
            .onEach { result ->
                when (result) {
                    is Result.Success -> processSuccess(result.data)
                    is Result.Error -> showError(result.exception.message ?: "Неизвестная ошибка")
                    is Result.NetworkError -> showError("Ошибка сети. Проверьте подключение.")
                }
            }.launchIn(viewModelScope)
    }

    private fun processSuccess(data: AnalysisData) {
        val analysisUiModel = mapper.map(data)
        _uiState.value = AnalysisUiState.Content(analysisUiModel, transactionType, parentRoute)

        if (analysisUiModel.categorySpents.isEmpty()) {
            showInfo("Нет данных за выбранный период")
        }
    }

    private fun showError(message: String) {
        viewModelScope.launch { snackbarHostState.showSnackbar(message) }
        _uiState.value = AnalysisUiState.Error(message)
    }

    private fun showInfo(message: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
        }
    }
}