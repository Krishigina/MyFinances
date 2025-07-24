package com.myfinances.di

import android.content.Context
import com.myfinances.data.manager.SnackbarManager
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        DataModule::class,
        DatabaseModule::class,
        RepositoryModule::class,
        MapperModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun viewModelComponentFactory(): ViewModelComponent.Factory

    fun customWorkerFactory(): CustomWorkerFactory

    fun provideSnackbarManager(): SnackbarManager
}