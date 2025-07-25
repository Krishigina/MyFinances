package com.myfinances.ui.screens.income

import com.myfinances.R
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.data.manager.SnackbarManager
import com.myfinances.data.manager.SyncUpdateManager
import com.myfinances.domain.entity.TransactionData
import com.myfinances.domain.usecase.GetIncomeTransactionsUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.TransactionDomainToUiMapper
import com.myfinances.ui.model.TransactionItemUiModel
import com.myfinances.ui.screens.common.BaseTransactionsViewModel
import com.myfinances.ui.screens.common.UiEvent
import com.myfinances.ui.util.ResourceProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

sealed interface IncomeEvent : UiEvent {
    data object Refresh : IncomeEvent
}

class IncomeViewModel @Inject constructor(
    private val getIncomeTransactionsUseCase: GetIncomeTransactionsUseCase,
    accountUpdateManager: AccountUpdateManager,
    syncUpdateManager: SyncUpdateManager,
    snackbarManager: SnackbarManager,
    mapper: TransactionDomainToUiMapper,
    resourceProvider: ResourceProvider
) : BaseTransactionsViewModel<IncomeUiState, IncomeEvent>(accountUpdateManager, syncUpdateManager, snackbarManager, mapper, resourceProvider) {

    init {
        startDataCollection()
    }

    override fun onEvent(event: IncomeEvent) {
        when (event) {
            IncomeEvent.Refresh -> refreshData(showLoading = true)
        }
    }

    override fun getInitialState(): IncomeUiState = IncomeUiState.Loading
    override fun getLoadingState(): IncomeUiState = IncomeUiState.Loading
    override fun isContentState(state: IncomeUiState): Boolean = state is IncomeUiState.Content

    override fun createContentState(items: List<TransactionItemUiModel>, total: String): IncomeUiState =
        IncomeUiState.Content(
            transactionItems = items,
            totalAmountFormatted = total
        )

    override fun getEmptyDataMessage(): Int = R.string.snackbar_no_income_today

    override fun getDataFlow(): Flow<Result<TransactionData>> = getIncomeTransactionsUseCase()

    override suspend fun refreshDataUseCase(): Result<Unit> = getIncomeTransactionsUseCase.refresh()
}