package com.myfinances.di

import androidx.lifecycle.ViewModelProvider
import com.myfinances.di.scopes.ViewModelScope
import dagger.Subcomponent

@ViewModelScope
@Subcomponent(modules = [ViewModelModule::class, DomainModule::class])
interface ViewModelComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ViewModelComponent
    }

    fun getViewModelFactory(): ViewModelProvider.Factory
}