package com.myfinances.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.myfinances.MyFinancesApplication

/**
 * Composable-функция для предоставления ViewModelProvider.Factory,
 * созданной с помощью Dagger.
 *
 * Она инкапсулирует логику получения AppComponent, создания
 * ViewModelComponent и извлечения из него фабрики.
 * `remember` используется для того, чтобы ViewModelComponent жил
 * на протяжении жизни Composable (и переживал рекомпозиции).
 */
@Composable
fun provideViewModelFactory(): ViewModelProvider.Factory {
    val context = LocalContext.current
    val appComponent = (context.applicationContext as MyFinancesApplication).appComponent

    // Мы создаем и запоминаем ViewModelComponent.
    // Его жизненный цикл будет привязан к месту вызова в дереве Composable.
    val viewModelComponent = remember {
        appComponent.viewModelComponentFactory().create()
    }

    return viewModelComponent.getViewModelFactory()
}