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
import com.myfinances.domain.repository.SyncRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MyFinancesApplication : Application() {

    lateinit var appComponent: AppComponent
        private set

    @Inject
    lateinit var syncRepository: SyncRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
        appComponent.inject(this)

        val customWorkerFactory = appComponent.customWorkerFactory()

        val workManagerConfig = Configuration.Builder()
            .setWorkerFactory(customWorkerFactory)
            .build()

        WorkManager.initialize(this, workManagerConfig)

        triggerInitialSync()
        setupPeriodicSync()
    }

    /**
     * Запускает разовую полную синхронизацию данных в фоновом потоке.
     */
    private fun triggerInitialSync() {
        applicationScope.launch {
            syncRepository.syncData()
        }
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