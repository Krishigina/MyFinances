package com.myfinances

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Главный класс Application, который служит точкой входа для Hilt
 * и инициализации глобальных компонентов приложения.
 */
@HiltAndroidApp
class MyFinancesApplication : Application()