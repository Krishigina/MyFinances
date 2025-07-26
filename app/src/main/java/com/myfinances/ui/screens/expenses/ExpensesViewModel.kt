package com.myfinances.ui.screens.expenses

import com.myfinances.R
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.data.manager.SnackbarManager
import com.myfinances.data.manager.SyncUpdateManager
import com.myfinances.domain.entity.TransactionData
import com.myfinances.domain.usecase.GetExpenseTransactionsUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.TransactionDomainToUiMapper
import com.myfinances.ui.model.TransactionItemUiModel
import com.myfinances.ui.screens.common.BaseTransactionsViewModel
import com.myfinances.ui.screens.common.UiEvent
import com.myfinances.ui.util.ResourceProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

sealed interface ExpensesEvent : UiEvent {
    data object Refresh : ExpensesEvent
}

class ExpensesViewModel @Inject constructor(
    private val getExpenseTransactionsUseCase: GetExpenseTransactionsUseCase,
    accountUpdateManager: AccountUpdateManager,
    syncUpdateManager: SyncUpdateManager,
    snackbarManager: SnackbarManager,
    mapper: TransactionDomainToUiMapper,
    resourceProvider: ResourceProvider
) : BaseTransactionsViewModel<ExpensesUiState, ExpensesEvent>(accountUpdateManager, syncUpdateManager, snackbarManager, mapper, resourceProvider) {

    init {
        startDataCollection()
    }

    override fun onEvent(event: ExpensesEvent) {
        when (event) {
            ExpensesEvent.Refresh -> refreshData(showLoading = true)
        }
    }

    override fun getInitialState(): ExpensesUiState = ExpensesUiState.Loading
    override fun getLoadingState(): ExpensesUiState = ExpensesUiState.Loading
    override fun isContentState(state: ExpensesUiState): Boolean = state is ExpensesUiState.Content

    override fun createContentState(items: List<TransactionItemUiModel>, total: String): ExpensesUiState =
        ExpensesUiState.Content(
            transactionItems = items,
            totalAmountFormatted = total
        )

    override fun getEmptyDataMessage(): Int = R.string.snackbar_no_expenses_today

    override fun getDataFlow(): Flow<Result<TransactionData>> = getExpenseTransactionsUseCase()

    override suspend fun refreshDataUseCase(): Result<Unit> = getExpenseTransactionsUseCase.refresh()
}