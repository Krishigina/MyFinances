package com.myfinances.di

import android.content.Context
import com.myfinances.MainActivity
import com.myfinances.MyFinancesApplication
import com.myfinances.data.manager.HapticFeedbackManager
import com.myfinances.data.manager.SnackbarManager
import com.myfinances.domain.usecase.IsPinSetUseCase
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
        MapperModule::class,
        DomainModule::class
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

    fun provideHapticFeedbackManager(): HapticFeedbackManager

    fun isPinSetUseCase(): IsPinSetUseCase

    fun inject(activity: MainActivity)
    fun inject(application: MyFinancesApplication)
}