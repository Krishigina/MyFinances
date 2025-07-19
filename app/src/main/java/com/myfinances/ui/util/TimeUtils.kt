package com.myfinances.ui.util

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.Locale

fun formatSyncTime(timestamp: Long?): String {
    if (timestamp == null) return "Никогда"

    val now = System.currentTimeMillis()
    return if (now - timestamp < DateUtils.MINUTE_IN_MILLIS) {
        "Только что"
    } else if (DateUtils.isToday(timestamp)) {
        "Сегодня, " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp)
    } else {
        DateUtils.getRelativeTimeSpanString(
            timestamp,
            now,
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE
        ).toString()
    }
}