package com.myfinances.ui.screens.expenses

import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.domain.entity.TransactionData
import com.myfinances.domain.usecase.GetExpenseTransactionsUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.mappers.TransactionDomainToUiMapper
import com.myfinances.ui.screens.common.BaseTransactionsViewModel
import javax.inject.Inject

class ExpensesViewModel @Inject constructor(
    private val getExpenseTransactionsUseCase: GetExpenseTransactionsUseCase,
    accountUpdateManager: AccountUpdateManager,
    mapper: TransactionDomainToUiMapper
) : BaseTransactionsViewModel<ExpensesUiState>(accountUpdateManager, mapper) {

    init {
        loadData()
    }

    override suspend fun getTransactionsUseCase(): Result<TransactionData> =
        getExpenseTransactionsUseCase()

    override fun getInitialLoadingState(): ExpensesUiState = ExpensesUiState.Loading

    override fun isContentState(state: ExpensesUiState): Boolean = state is ExpensesUiState.Content

    override fun createContentState(
        items: List<ListItemModel>,
        total: String
    ): ExpensesUiState = ExpensesUiState.Content(
        transactionItems = items,
        totalAmountFormatted = total
    )

    override fun getEmptyDataMessage(): String = "За сегодня еще не было расходов"
}