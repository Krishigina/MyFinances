package com.myfinances.domain.usecase

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.myfinances.data.workers.SyncWorker
import com.myfinances.domain.entity.SyncFrequency
import com.myfinances.domain.repository.SessionRepository
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class SetupPeriodicSyncUseCase @Inject constructor(
    private val context: Context,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke() {
        val workManager = WorkManager.getInstance(context)
        val frequency = sessionRepository.getSyncFrequency().first()

        if (frequency == SyncFrequency.NEVER) {
            workManager.cancelUniqueWork(SyncWorker.WORK_NAME)
            return
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(frequency.hours, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            syncRequest
        )
    }
}