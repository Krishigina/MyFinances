package com.myfinances.domain.repository

import com.myfinances.domain.util.Result

interface SyncRepository {
    suspend fun syncData(): Result<Unit>
}