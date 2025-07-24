package com.myfinances.ui.screens.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.data.manager.SnackbarManager
import com.myfinances.domain.entity.AnalysisData
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.usecase.GetAnalysisDataUseCase
import com.myfinances.domain.util.Result
import com.myfinances.domain.util.withTimeAtStartOfDay
import com.myfinances.ui.mappers.AnalysisDomainToUiMapper
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
    private val snackbarManager: SnackbarManager,
    private val mapper: AnalysisDomainToUiMapper
) : ViewModel() {

    private lateinit var transactionType: TransactionTypeFilter
    private lateinit var parentRoute: String

    private val _uiState = MutableStateFlow<AnalysisUiState>(AnalysisUiState.Loading)
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

        observeData(startDate, endDate)
        refreshData(startDate, endDate, showLoading = true)

        viewModelScope.launch {
            accountUpdateManager.accountUpdateFlow.collect {
                (_uiState.value as? AnalysisUiState.Content)?.let {
                    observeData(it.uiModel.startDate, it.uiModel.endDate)
                    refreshData(it.uiModel.startDate, it.uiModel.endDate, showLoading = false)
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
                    observeData(newStartDate, contentState.uiModel.endDate)
                    refreshData(newStartDate, contentState.uiModel.endDate, showLoading = true)
                }
            }
            is AnalysisEvent.EndDateSelected -> {
                val newEndDate = Date(event.timestampMillis)
                if (!newEndDate.before(contentState.uiModel.startDate)) {
                    observeData(contentState.uiModel.startDate, newEndDate)
                    refreshData(contentState.uiModel.startDate, newEndDate, showLoading = true)
                }
            }
        }
    }

    private fun refreshData(startDate: Date, endDate: Date, showLoading: Boolean) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = AnalysisUiState.Loading
            }
            when (val refreshResult = getAnalysisDataUseCase.refresh(startDate, endDate)) {
                is Result.Success -> {}
                is Result.Failure -> {
                    val message = when (refreshResult) {
                        is Result.Failure.ApiError -> "Ошибка API при обновлении"
                        is Result.Failure.GenericError -> refreshResult.exception.message ?: "Ошибка обновления"
                        is Result.Failure.NetworkError -> "Нет сети. Отображены локальные данные."
                    }
                    snackbarManager.showMessage(message)
                }
            }
        }
    }

    private fun observeData(startDate: Date, endDate: Date) {
        dataCollectionJob?.cancel()
        dataCollectionJob = getAnalysisDataUseCase(startDate, endDate, transactionType)
            .onEach { result ->
                when (result) {
                    is Result.Success -> processSuccess(result.data)
                    is Result.Failure -> {
                        val message = when (result) {
                            is Result.Failure.ApiError -> "Ошибка API: ${result.code}"
                            is Result.Failure.GenericError -> result.exception.message ?: "Неизвестная ошибка"
                            is Result.Failure.NetworkError -> "Ошибка сети. Проверьте подключение."
                        }
                        _uiState.value = AnalysisUiState.Error(message)
                        snackbarManager.showMessage(message)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun processSuccess(data: AnalysisData) {
        val analysisUiModel = mapper.map(data)
        _uiState.value = AnalysisUiState.Content(analysisUiModel, transactionType, parentRoute)
    }
}