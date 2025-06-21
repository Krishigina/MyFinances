package com.myfinances.ui.screens.history

sealed interface HistoryEvent {
    data class StartDateSelected(val timestampMillis: Long) : HistoryEvent
    data class EndDateSelected(val timestampMillis: Long) : HistoryEvent
}