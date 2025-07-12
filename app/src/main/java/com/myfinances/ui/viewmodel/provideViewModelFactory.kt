package com.myfinances.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.myfinances.MyFinancesApplication

@Composable
fun provideViewModelFactory(): ViewModelProvider.Factory {
    val owner: SavedStateRegistryOwner = LocalSavedStateRegistryOwner.current
    val appComponent =
        (LocalContext.current.applicationContext as MyFinancesApplication).appComponent

    return remember(owner) {
        appComponent.viewModelComponentFactory().create().getViewModelFactory().create(owner)
    }
}