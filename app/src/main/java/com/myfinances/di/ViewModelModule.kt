package com.myfinances.di

import androidx.lifecycle.ViewModel
import com.myfinances.ui.screens.account.AccountViewModel
import com.myfinances.ui.screens.add_edit_transaction.AddEditTransactionViewModel
import com.myfinances.ui.screens.articles.ArticlesViewModel
import com.myfinances.ui.screens.expenses.ExpensesViewModel
import com.myfinances.ui.screens.history.HistoryViewModel
import com.myfinances.ui.screens.income.IncomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ExpensesViewModel::class)
    abstract fun bindExpensesViewModel(viewModel: ExpensesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IncomeViewModel::class)
    abstract fun bindIncomeViewModel(viewModel: IncomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArticlesViewModel::class)
    abstract fun bindArticlesViewModel(viewModel: ArticlesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(viewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @AssistedViewModelKey(HistoryViewModel::class)
    abstract fun bindHistoryViewModelAssistedFactory(
        factory: HistoryViewModel.Factory
    ): ViewModelAssistedFactory<out ViewModel>

    @Binds
    @IntoMap
    @AssistedViewModelKey(AddEditTransactionViewModel::class)
    abstract fun bindAddEditTransactionViewModelAssistedFactory(
        factory: AddEditTransactionViewModel.Factory
    ): ViewModelAssistedFactory<out ViewModel>
}