package com.ayodkay.apps.swen.networking.interceptor

import com.ayodkay.apps.swen.helper.AppLog
import com.ayodkay.apps.swen.networking.constants.Constant.Companion.HEADER_CACHE_CONTROL
import com.ayodkay.apps.swen.networking.constants.Constant.Companion.HEADER_PRAGMA
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class NetworkInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        AppLog.l("network interceptor: called.")

        val response = chain.proceed(chain.request())

        val cacheControl = CacheControl.Builder()
            .maxAge(1, TimeUnit.HOURS)
            .build()

        return response.newBuilder()
            .removeHeader(HEADER_PRAGMA)
            .removeHeader(HEADER_CACHE_CONTROL)
            .header(HEADER_CACHE_CONTROL, cacheControl.toString())
            .build()
    }
}