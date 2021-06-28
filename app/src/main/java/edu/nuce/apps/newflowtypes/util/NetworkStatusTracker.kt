package edu.nuce.apps.newflowtypes.util

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import edu.nuce.apps.newflowtypes.di.ApplicationScope
import edu.nuce.apps.newflowtypes.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

enum class NetworkStatus {
    Available,
    Unavailable
}

enum class NetworkState {
    Fetched,
    Failure
}

@ExperimentalCoroutinesApi
@Singleton
class NetworkStatusTracker @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    val networkStatus: SharedFlow<NetworkStatus> = callbackFlow {
        val networkStatusCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onUnavailable() {
                Timber.d("onUnavailable")
                tryOffer(NetworkStatus.Unavailable)
            }

            override fun onAvailable(network: Network) {
                Timber.d("onAvailable")
                tryOffer(NetworkStatus.Available)
            }

            override fun onLost(network: Network) {
                Timber.d("onLost")
                tryOffer(NetworkStatus.Unavailable)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkStatusCallback)

        awaitClose { connectivityManager.unregisterNetworkCallback(networkStatusCallback) }
    }
        .flowOn(ioDispatcher)
        .distinctUntilChanged()
        .shareIn(
            scope = externalScope,
            started = SharingStarted.WhileSubscribed(),
        )
}