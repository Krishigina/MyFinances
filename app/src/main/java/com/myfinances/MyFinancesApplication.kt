package com.myfinances

import android.app.Application
import com.myfinances.di.AppComponent
import com.myfinances.di.DaggerAppComponent

/**
 * Главный класс Application, который служит точкой входа для Hilt
 * и инициализации глобальных компонентов приложения.
 */

class MyFinancesApplication : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }
}