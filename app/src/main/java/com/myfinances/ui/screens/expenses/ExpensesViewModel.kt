package com.myfinances.ui.screens.expenses

import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.domain.entity.TransactionData
import com.myfinances.domain.usecase.GetExpenseTransactionsUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.TransactionDomainToUiMapper
import com.myfinances.ui.model.TransactionItemUiModel
import com.myfinances.ui.screens.common.BaseTransactionsViewModel
import com.myfinances.ui.screens.common.UiEvent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

sealed interface ExpensesEvent : UiEvent

class ExpensesViewModel @Inject constructor(
    private val getExpenseTransactionsUseCase: GetExpenseTransactionsUseCase,
    accountUpdateManager: AccountUpdateManager,
    mapper: TransactionDomainToUiMapper
) : BaseTransactionsViewModel<ExpensesUiState, ExpensesEvent>(accountUpdateManager, mapper) {

    init {
        startDataCollection()
    }

    override fun getInitialState(): ExpensesUiState = ExpensesUiState.Loading
    override fun getLoadingState(): ExpensesUiState = ExpensesUiState.Loading
    override fun isContentState(state: ExpensesUiState): Boolean = state is ExpensesUiState.Content

    override fun createContentState(items: List<TransactionItemUiModel>, total: String): ExpensesUiState =
        ExpensesUiState.Content(
            transactionItems = items,
            totalAmountFormatted = total
        )

    override fun getEmptyDataMessage(): String = "За сегодня еще не было расходов"

    override fun getDataFlow(): Flow<Result<TransactionData>> = getExpenseTransactionsUseCase()

    override suspend fun refreshDataUseCase(): Result<Unit> = getExpenseTransactionsUseCase.refresh()
}