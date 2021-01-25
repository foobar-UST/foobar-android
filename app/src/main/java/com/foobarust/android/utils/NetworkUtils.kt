package com.foobarust.android.utils

/**
 * Created by kevin on 12/31/20
 */

/*
class NetworkUtils @Inject constructor(@ApplicationContext val context: Context) {

    suspend fun hasNetworkConnection(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return suspendCancellableCoroutine { continuation ->
                val networkCallback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        connectivityManager.unregisterNetworkCallback(this)
                        continuation.resume(true)
                    }

                    override fun onLost(network: Network) {
                        super.onLost(network)
                        connectivityManager.unregisterNetworkCallback(this)
                        continuation.resume(false)
                    }
                }

                connectivityManager.registerDefaultNetworkCallback(networkCallback)

                continuation.invokeOnCancellation {
                    connectivityManager.unregisterNetworkCallback(networkCallback)
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
        }
    }
}

 */
