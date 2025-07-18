package com.myfinances.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.myfinances.data.workers.SyncWorker
import com.myfinances.domain.repository.SyncRepository
import javax.inject.Inject
import javax.inject.Provider

class CustomWorkerFactory @Inject constructor(
    private val syncRepositoryProvider: Provider<SyncRepository>
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SyncWorker::class.java.name ->
                SyncWorker(
                    appContext,
                    workerParameters,
                    syncRepositoryProvider.get()
                )
            else -> null
        }
    }
}