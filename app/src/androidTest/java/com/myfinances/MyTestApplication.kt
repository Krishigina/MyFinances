package com.myfinances

import android.util.Log
import com.myfinances.di.DaggerAppComponent

/**
 * Кастомный Application класс для использования в инструментальных тестах.
 * Он наследуется от основного класса приложения, но переопределяет методы,
 * которые запускают фоновые задачи (сетевую синхронизацию, WorkManager),
 * чтобы они не выполнялись во время UI-тестов. Это делает тесты
 * стабильными, быстрыми и изолированными от внешних зависимостей.
 */
class MyTestApplication : MyFinancesApplication() {
    override fun onCreate() {
        super.onCreate()
        Log.i("MyTestApplication", "Test application is running, sync is disabled.")
    }

    /**
     * Пересоздает Dagger AppComponent. Это необходимо для полной изоляции
     * состояния между UI-тестами, так как сбрасываются все синглтоны.
     */
    fun resetAppComponent() {
        appComponent = DaggerAppComponent.factory().create(this)
        appComponent.inject(this)
        Log.i("MyTestApplication", "Dagger AppComponent has been reset for a new test.")
    }

    override fun triggerInitialSync() {

    }

    override fun setupPeriodicSync() {

    }
}