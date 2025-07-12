package com.myfinances.di

import androidx.lifecycle.ViewModelProvider
import com.myfinances.di.scopes.ViewModelScope
import com.myfinances.ui.screens.add_edit_transaction.AddEditTransactionViewModel
import dagger.Subcomponent

@ViewModelScope
@Subcomponent(modules = [ViewModelModule::class, DomainModule::class])
interface ViewModelComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ViewModelComponent
    }
    fun getViewModelFactory(): ViewModelProvider.Factory

    fun getAddEditTransactionViewModelFactory(): AddEditTransactionViewModel.Factory
}