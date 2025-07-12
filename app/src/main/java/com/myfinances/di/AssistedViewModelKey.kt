package com.myfinances.di

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Кастомный MapKey для предоставления ассистированных фабрик ViewModel через Dagger.
 * Это позволяет нам отличать их от обычных провайдеров ViewModel в мультибиндингах.
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class AssistedViewModelKey(val value: KClass<out ViewModel>)