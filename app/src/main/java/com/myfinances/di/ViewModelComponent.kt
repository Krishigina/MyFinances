package com.myfinances.di

import androidx.lifecycle.ViewModelProvider
import com.myfinances.di.scopes.ViewModelScope
import dagger.Subcomponent

/**
 * Сабкомпонент Dagger, предназначенный для предоставления зависимостей
 * с жизненным циклом ViewModel.
 *
 * Он наследует все зависимости из родительского AppComponent.
 */
@ViewModelScope
@Subcomponent(modules = [ViewModelModule::class, DomainModule::class])
interface ViewModelComponent {

    /**
     * Фабрика для создания экземпляров ViewModelComponent.
     * Dagger автоматически сгенерирует реализацию.
     */
    @Subcomponent.Factory
    interface Factory {
        fun create(): ViewModelComponent
    }

    /**
     * Предоставляет фабрику ViewModel для создания экземпляров ViewModel
     * с внедренными зависимостями из этого компонента.
     */
    fun getViewModelFactory(): ViewModelProvider.Factory
}