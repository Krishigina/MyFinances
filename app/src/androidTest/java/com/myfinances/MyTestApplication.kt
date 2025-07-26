package com.myfinances

import android.util.Log

/**
 * Кастомный Application класс для использования в инструментальных тестах.
 * Он наследуется от основного класса приложения, но переопределяет методы,
 * которые запускают фоновые задачи (сетевую синхронизацию, WorkManager),
 * чтобы они не выполнялись во время UI-тестов. Это делает тесты
 * стабильными, быстрыми и изолированными от внешних зависимостей.
 */
class MyTestApplication : MyFinancesApplication() {
    override fun onCreate() {
        // Мы намеренно вызываем super.onCreate() ПОСЛЕ наших переопределений,
        // но в данном случае лучше полностью контролировать процесс и не вызывать
        // triggerInitialSync и setupPeriodicSync вообще.
        // Поэтому мы скопируем логику из родительского onCreate, исключив лишнее.
        super.onCreate()
        Log.i("MyTestApplication", "Test application is running, sync is disabled.")
    }

    override fun triggerInitialSync() {
        // Оставляем пустым, чтобы не запускать сетевую синхронизацию в тестах
    }

    override fun setupPeriodicSync() {
        // Оставляем пустым, чтобы не настраивать WorkManager в тестах
    }
}