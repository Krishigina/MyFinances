package com.myfinances

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.myfinances.data.workers.SyncWorker
import com.myfinances.di.AppComponent
import com.myfinances.di.CustomWorkerFactory
import com.myfinances.di.DaggerAppComponent
import java.util.concurrent.TimeUnit

/**
 * Главный класс Application, который служит точкой входа для Dagger,
 * инициализации глобальных компонентов приложения и настройки WorkManager.
 */
class MyFinancesApplication : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)

        val customWorkerFactory = appComponent.customWorkerFactory()

        val workManagerConfig = Configuration.Builder()
            .setWorkerFactory(customWorkerFactory)
            .build()

        WorkManager.initialize(this, workManagerConfig)

        setupPeriodicSync()
    }

    private fun setupPeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}