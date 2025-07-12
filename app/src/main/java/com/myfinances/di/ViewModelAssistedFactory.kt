package com.myfinances.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 * Общий интерфейс для фабрик ViewModel, которые используют @AssistedInject.
 * Это позволяет создать универсальную ViewModelProvider.Factory, которая может
 * обрабатывать создание ViewModel как с обычными, так и с ассистированными зависимостями.
 *
 * @param T Тип ViewModel, которую создает фабрика.
 */
interface ViewModelAssistedFactory<T : ViewModel> {
    fun create(handle: SavedStateHandle): T
}