package com.foobarust.android.utils

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.util.Log
import com.foobarust.domain.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by kevin on 12/31/20
 */

@Singleton
class NetworkUtils @Inject constructor(
    @ApplicationScope private val coroutineScope: CoroutineScope,
    private val connectivityManager: ConnectivityManager
) {

    val networkStateFlow: SharedFlow<NetworkState> = callbackFlow<NetworkState> {
        val builder = NetworkRequest.Builder()
        val listener = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                channel.offer(NetworkState.AVAILABLE)
            }

            override fun onLost(network: Network) {
                channel.offer(NetworkState.DISCONNECTED)
            }
        }

        connectivityManager.registerNetworkCallback(
            builder.build(),
            listener
        )

        awaitClose { connectivityManager.unregisterNetworkCallback(listener) }
    }
        .onEach {
            Log.d("NetworkUtils", "State: $it")
        }
        .distinctUntilChanged()
        .shareIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1
        )
}

enum class NetworkState {
    AVAILABLE,
    DISCONNECTED
}
