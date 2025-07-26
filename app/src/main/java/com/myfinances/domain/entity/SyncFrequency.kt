package com.myfinances.domain.entity

enum class SyncFrequency(val hours: Long) {
    H3(3),
    H6(6),
    H12(12),
    H24(24),
    NEVER(0);

    companion object {
        val default = H6

        fun fromHours(hours: Long): SyncFrequency {
            return entries.find { it.hours == hours } ?: default
        }
    }
}