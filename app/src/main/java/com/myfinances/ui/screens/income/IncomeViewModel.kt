package com.myfinances.ui.screens.income

import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.domain.entity.TransactionData
import com.myfinances.domain.usecase.GetIncomeTransactionsUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.mappers.TransactionDomainToUiMapper
import com.myfinances.ui.model.TransactionItemUiModel
import com.myfinances.ui.screens.common.BaseTransactionsViewModel
import javax.inject.Inject

class IncomeViewModel @Inject constructor(
    private val getIncomeTransactionsUseCase: GetIncomeTransactionsUseCase,
    accountUpdateManager: AccountUpdateManager,
    mapper: TransactionDomainToUiMapper
) : BaseTransactionsViewModel<IncomeUiState>(accountUpdateManager, mapper) {

    init {
        loadData()
    }

    override suspend fun getTransactionsUseCase(): Result<TransactionData> =
        getIncomeTransactionsUseCase()

    override fun getInitialLoadingState(): IncomeUiState = IncomeUiState.Loading

    override fun isContentState(state: IncomeUiState): Boolean = state is IncomeUiState.Content

    override fun createContentState(
        items: List<TransactionItemUiModel>,
        total: String
    ): IncomeUiState = IncomeUiState.Content(
        transactionItems = items,
        totalAmountFormatted = total
    )

    override fun getEmptyDataMessage(): String = "За сегодня еще не было доходов"
}