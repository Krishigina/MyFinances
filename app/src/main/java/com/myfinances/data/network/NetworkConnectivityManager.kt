package com.myfinances.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация [ConnectivityManagerSource], которая использует системный
 * [ConnectivityManager] для отслеживания состояния сети.
 */
@Singleton
class NetworkConnectivityManager @Inject constructor(
    private val context: Context
) : ConnectivityManagerSource {
    override val isNetworkAvailable: Flow<Boolean> = callbackFlow {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                channel.trySend(true)
            }

            override fun onLost(network: Network) {
                channel.trySend(false)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        // Проверка начального состояния
        val initialNetwork = connectivityManager.activeNetwork
        channel.trySend(initialNetwork != null)

        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.conflate()
}