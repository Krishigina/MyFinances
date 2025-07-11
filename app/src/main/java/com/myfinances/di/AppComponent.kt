package com.myfinances.di

import android.content.Context
import com.myfinances.ui.screens.account.AccountViewModel
import com.myfinances.ui.screens.articles.ArticlesViewModel
import com.myfinances.ui.screens.expenses.ExpensesViewModel
import com.myfinances.ui.screens.history.HistoryViewModel
import com.myfinances.ui.screens.income.IncomeViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DataModule::class, RepositoryModule::class, ViewModelModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(viewModel: ExpensesViewModel)
    fun inject(viewModel: IncomeViewModel)
    fun inject(viewModel: ArticlesViewModel)
    fun inject(viewModel: AccountViewModel)
    fun inject(viewModel: HistoryViewModel)

    fun getViewModelFactory(): ViewModelFactory
}