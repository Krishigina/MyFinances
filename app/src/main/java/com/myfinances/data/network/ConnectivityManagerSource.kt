package com.myfinances.data.network

import kotlinx.coroutines.flow.Flow

/**
 * Абстракция для источника данных о состоянии сети.
 * Позволяет отслеживать доступность интернет-соединения в реальном времени.
 */
interface ConnectivityManagerSource {
    val isNetworkAvailable: Flow<Boolean>
}