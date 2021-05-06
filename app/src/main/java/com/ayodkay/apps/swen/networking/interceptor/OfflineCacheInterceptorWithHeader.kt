package com.ayodkay.apps.swen.networking.interceptor

import android.content.Context
import android.net.ConnectivityManager
import com.ayodkay.apps.swen.helper.App
import com.ayodkay.apps.swen.helper.AppLog
import com.ayodkay.apps.swen.networking.constants.Constant.Companion.HEADER_CACHE_CONTROL
import com.ayodkay.apps.swen.networking.constants.Constant.Companion.HEADER_PRAGMA
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

open class OfflineCacheInterceptorWithHeader : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val header = request.headers["CacheAble"]

        if (header != null) {
            /* check if this request has the [CacheAble] header */
            if (header == "true" &&
                !isNetworkConnected()
            ) {
                AppLog.l("CACHE Header: called.::%s $header")

                // prevent caching when network is on. For that we use the "networkInterceptor"
                AppLog.l("cache interceptor: called.")
                val cacheControl = CacheControl.Builder()
                    .maxStale(1, TimeUnit.HOURS)
                    .build()

                request = request.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build()
            } else {
                AppLog.l("cache interceptor: not called.")
            }
        }
        return chain.proceed(request)
    }

    private fun isNetworkConnected(): Boolean {
        val cm: ConnectivityManager =
            App.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
    }
}