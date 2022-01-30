package com.ayodkay.apps.swen.helper.backend

import android.content.Context
import android.net.ConnectivityManager
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object MyReachability {
    const val REACHABILITY_SERVER = "https://www.google.com"
    const val LANDING_SERVER = "https://www.myserver.com"

    private fun hasNetworkAvailable(context: Context): Boolean {
        val service = Context.CONNECTIVITY_SERVICE
        val manager = context.getSystemService(service) as ConnectivityManager?
        val network = manager?.activeNetworkInfo
        return (network?.isConnected) ?: false
    }

    fun hasInternetConnected(context: Context): Boolean {
        if (hasNetworkAvailable(context)) {
            try {
                val connection = URL(REACHABILITY_SERVER).openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "Test")
                connection.setRequestProperty("Connection", "close")
                connection.connectTimeout = 1500 // configurable
                connection.connect()
                return (connection.responseCode == 200)
            } catch (e: IOException) {
            }
        } else {
        }
        return false
    }

    fun hasServerConnected(context: Context): Boolean {
        if (hasNetworkAvailable(context)) {
            try {
                val connection = URL(LANDING_SERVER).openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "Test")
                connection.setRequestProperty("Connection", "close")
                connection.connectTimeout = 1500 // configurable
                connection.connect()
                return (connection.responseCode == 200)
            } catch (e: IOException) {
            }
        } else {
        }
        return false
    }
}