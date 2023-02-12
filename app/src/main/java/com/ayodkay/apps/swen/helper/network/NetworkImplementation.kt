package com.ayodkay.apps.swen.helper.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NetworkImplementation : NetworkInterface, KoinComponent {
    private val context: Context by inject()
    private val isNetworkConnected = MutableStateFlow(false)
    private var connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val request =
        NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

    private val networkCallback: ConnectivityManager.NetworkCallback =
        object : ConnectivityManager.NetworkCallback() {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onAvailable(network: Network) {
                isNetworkConnected.value = isConnected()
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onUnavailable() {
                isNetworkConnected.value = isConnected()
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onLost(network: Network) {
                isNetworkConnected.value = isConnected()
            }
        }

    init {
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    override fun netWorkConnected(): StateFlow<Boolean> {
        return isNetworkConnected
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isConnected(): Boolean {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}
