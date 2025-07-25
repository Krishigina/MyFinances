package com.myfinances.ui.util

import android.text.format.DateUtils
import com.myfinances.R
import java.text.SimpleDateFormat
import java.util.Locale

fun formatSyncTime(timestamp: Long?, resourceProvider: ResourceProvider): String {
    if (timestamp == null) return resourceProvider.getString(R.string.time_never)

    val now = System.currentTimeMillis()
    return if (now - timestamp < DateUtils.MINUTE_IN_MILLIS) {
        resourceProvider.getString(R.string.time_just_now)
    } else if (DateUtils.isToday(timestamp)) {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp)
        resourceProvider.getString(R.string.time_today_at, time)
    } else {
        DateUtils.getRelativeTimeSpanString(
            timestamp,
            now,
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE
        ).toString()
    }
}