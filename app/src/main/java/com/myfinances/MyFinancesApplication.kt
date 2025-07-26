// Файл: app/src/main/java/com/myfinances/MyFinancesApplication.kt

package com.myfinances

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.myfinances.di.AppComponent
import com.myfinances.di.DaggerAppComponent
import com.myfinances.domain.repository.SyncRepository
import com.myfinances.domain.usecase.SetupPeriodicSyncUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

open class MyFinancesApplication : Application() {

    lateinit var appComponent: AppComponent
        protected set

    @Inject
    lateinit var syncRepository: SyncRepository

    @Inject
    lateinit var setupPeriodicSyncUseCase: SetupPeriodicSyncUseCase

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

    open fun triggerInitialSync() {
        applicationScope.launch {
            syncRepository.syncData()
        }
    }
    open fun setupPeriodicSync() {
        applicationScope.launch {
            setupPeriodicSyncUseCase()
        }
    }
}