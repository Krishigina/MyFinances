package com.myfinances.di.scopes

import javax.inject.Scope

/**
 * Кастомный скоуп для зависимостей, жизненный цикл которых
 * привязан к жизненному циклу ViewModel.
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ViewModelScope