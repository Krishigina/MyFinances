package com.myfinances.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.myfinances.MyFinancesApplication


@Composable
fun provideViewModelFactory(): ViewModelProvider.Factory {
    val appComponent =
        (LocalContext.current.applicationContext as MyFinancesApplication).appComponent
    return appComponent.viewModelComponentFactory().create().getViewModelFactory()
}