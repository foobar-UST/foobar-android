package com.foobarust.android.utils

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Created by kevin on 12/31/20
 */

private const val TAG = "NetworkStateFlow"

fun ConnectivityManager.hasNetworkConnection(): Flow<Boolean> = callbackFlow<Boolean> {
    val builder = NetworkRequest.Builder()
    val validNetworks: MutableSet<Network> = HashSet()

    val listener = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            // Check has internet capability
            val networkCapabilities = getNetworkCapabilities(network)
            val hasInternetCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)

            if (hasInternetCapability == true) {
                launch(Dispatchers.IO) {
                    val hasInternet = checkNetworkHaveInternet()
                    if (hasInternet) {
                        validNetworks.add(network)
                        checkValidNetworks(validNetworks)
                    }
                }
            }
        }

        override fun onLost(network: Network) {
            validNetworks.remove(network)
            checkValidNetworks(validNetworks)
        }
    }

    registerNetworkCallback(builder.build(), listener)

    awaitClose { unregisterNetworkCallback(listener) }
}
    .distinctUntilChanged()

private fun ProducerScope<Boolean>.checkValidNetworks(validNetworks: MutableSet<Network>) {
    if (validNetworks.isNotEmpty()) {
        channel.offer(true)
    } else {
        channel.offer(false)
    }
}

private fun checkNetworkHaveInternet(): Boolean {
    Log.d(TAG, "Pinging google to check for connection.")
    return try {
        Socket().use {
            it.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            Log.d(TAG, "Connected.")
            true
        }
    } catch (e: Exception) {
        Log.d(TAG, "No internet connection: $e")
        false
    }
}
