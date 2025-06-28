package com.myfinances.domain.util

import java.util.Calendar

/**
 * Устанавливает время для объекта Calendar на самое начало дня (00:00:00.000).
 */
fun Calendar.withTimeAtStartOfDay(): Calendar {
    this.set(Calendar.HOUR_OF_DAY, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.SECOND, 0)
    this.set(Calendar.MILLISECOND, 0)
    return this
}

/**
 * Устанавливает время для объекта Calendar на самый конец дня (23:59:59.999).
 */
fun Calendar.withTimeAtEndOfDay(): Calendar {
    this.set(Calendar.HOUR_OF_DAY, 23)
    this.set(Calendar.MINUTE, 59)
    this.set(Calendar.SECOND, 59)
    this.set(Calendar.MILLISECOND, 999)
    return this
}