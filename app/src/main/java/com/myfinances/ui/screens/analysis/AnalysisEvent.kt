package com.myfinances.ui.screens.analysis

sealed interface AnalysisEvent {
    data class StartDateSelected(val timestampMillis: Long) : AnalysisEvent
    data class EndDateSelected(val timestampMillis: Long) : AnalysisEvent
}