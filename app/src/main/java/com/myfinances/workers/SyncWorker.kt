package com.myfinances.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.myfinances.domain.repository.SyncRepository

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "com.myfinances.data.workers.SyncWorker"
    }

    override suspend fun doWork(): Result {
        Log.i("SyncWorker", "Sync worker started.")
        return when (syncRepository.syncData()) {
            is com.myfinances.domain.util.Result.Success -> {
                Log.i("SyncWorker", "Sync successful.")
                Result.success()
            }
            is com.myfinances.domain.util.Result.Failure.NetworkError -> {
                Log.w("SyncWorker", "Sync failed due to network error, will retry.")
                Result.retry()
            }
            is com.myfinances.domain.util.Result.Failure.GenericError -> {
                Log.e("SyncWorker", "Sync failed with an error, will retry.")
                Result.retry()
            }

            is com.myfinances.domain.util.Result.Failure.ApiError -> TODO()
        }
    }
}