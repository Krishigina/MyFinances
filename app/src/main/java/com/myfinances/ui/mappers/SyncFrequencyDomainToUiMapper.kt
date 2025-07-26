package com.myfinances.ui.mappers

import com.myfinances.R
import com.myfinances.domain.entity.SyncFrequency
import com.myfinances.ui.util.ResourceProvider
import javax.inject.Inject

class SyncFrequencyDomainToUiMapper @Inject constructor(
    private val resourceProvider: ResourceProvider
) {
    fun map(frequency: SyncFrequency): String {
        return when (frequency) {
            SyncFrequency.H3 -> resourceProvider.getString(R.string.sync_frequency_3h)
            SyncFrequency.H6 -> resourceProvider.getString(R.string.sync_frequency_6h)
            SyncFrequency.H12 -> resourceProvider.getString(R.string.sync_frequency_12h)
            SyncFrequency.H24 -> resourceProvider.getString(R.string.sync_frequency_24h)
            SyncFrequency.NEVER -> resourceProvider.getString(R.string.sync_frequency_never)
        }
    }
}