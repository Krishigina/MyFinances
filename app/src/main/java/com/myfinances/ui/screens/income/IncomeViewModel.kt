package com.myfinances.ui.screens.income

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
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

sealed interface IncomeEvent : UiEvent

class IncomeViewModel @Inject constructor(
    private val getIncomeTransactionsUseCase: GetIncomeTransactionsUseCase,
    accountUpdateManager: AccountUpdateManager,
    syncUpdateManager: SyncUpdateManager,
    snackbarManager: SnackbarManager,
    mapper: TransactionDomainToUiMapper
) : BaseTransactionsViewModel<IncomeUiState, IncomeEvent>(accountUpdateManager, syncUpdateManager, snackbarManager, mapper) {

    init {
        startDataCollection()
    }

    override fun getInitialState(): IncomeUiState = IncomeUiState.Loading
    override fun getLoadingState(): IncomeUiState = IncomeUiState.Loading
    override fun isContentState(state: IncomeUiState): Boolean = state is IncomeUiState.Content

    override fun createContentState(items: List<TransactionItemUiModel>, total: String): IncomeUiState =
        IncomeUiState.Content(
            transactionItems = items,
            totalAmountFormatted = total
        )

    override fun getEmptyDataMessage(): String = "За сегодня еще не было доходов"

    override fun getDataFlow(): Flow<Result<TransactionData>> = getIncomeTransactionsUseCase()

    override suspend fun refreshDataUseCase(): Result<Unit> = getIncomeTransactionsUseCase.refresh()
}