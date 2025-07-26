package com.myfinances.di

import com.myfinances.di.scopes.ViewModelScope
import dagger.Subcomponent

@ViewModelScope
@Subcomponent(modules = [ViewModelModule::class])
interface ViewModelComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ViewModelComponent
    }

    fun getViewModelFactory(): ViewModelFactory
}