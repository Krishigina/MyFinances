package com.myfinances.data.network

import kotlinx.coroutines.flow.Flow

interface ConnectivityManagerSource {
    val isNetworkAvailable: Flow<Boolean>
}