package com.myfinances.di

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import javax.inject.Inject
import javax.inject.Provider

/**
 * Универсальная фабрика для ViewModel.
 * Умеет создавать как обычные ViewModel (@Inject), так и ViewModel с
 * ассистированными зависимостями (@AssistedInject), которым нужен SavedStateHandle.
 *
 * Эта фабрика сама не является ViewModelProvider.Factory, а является *создателем*
 * таких фабрик, привязанных к конкретному SavedStateRegistryOwner.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory @Inject constructor(
    private val creators: @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>>,
    private val assistedCreators: @JvmSuppressWildcards Map<Class<out ViewModel>, ViewModelAssistedFactory<out ViewModel>>
) {
    fun create(
        owner: SavedStateRegistryOwner,
        defaultArgs: Bundle? = null
    ): AbstractSavedStateViewModelFactory {
        return object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                // Сначала ищем ассистированную фабрику
                val assistedCreator = assistedCreators[modelClass]
                if (assistedCreator != null) {
                    return assistedCreator.create(handle) as T
                }

                // Если не нашли, ищем обычный провайдер
                val creator = creators[modelClass] ?: creators.entries.firstOrNull {
                    modelClass.isAssignableFrom(it.key)
                }?.value ?: throw IllegalArgumentException("unknown model class $modelClass")

                return try {
                    creator.get() as T
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
        }
    }
}