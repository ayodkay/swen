package com.ayodkay.apps.swen.networking.service

import com.ayodkay.apps.swen.helper.App.Companion.context
import com.ayodkay.apps.swen.helper.AppLog
import com.ayodkay.apps.swen.networking.interceptor.NetworkInterceptor
import com.ayodkay.apps.swen.networking.interceptor.OfflineCacheInterceptor
import com.ayodkay.apps.swen.networking.interceptor.OfflineCacheInterceptorWithHeader
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitService {
    // HttpLoggingInterceptor
    //https://medium.com/swlh/annotation-based-offline-caching-in-retrofit-d7dbd775ac74
    private val httpLoggingInterceptor =
        HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                AppLog.l(message)
            }
        })

    fun <S> createService(serviceClass: Class<S>): S {
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val networkInterceptor = NetworkInterceptor()
        val offlineCacheInterceptor = OfflineCacheInterceptor()
        val offlineCacheInterceptorWithHeader = OfflineCacheInterceptorWithHeader()

        // OkHttpClient
        val okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
            .cache(cache())
            .addInterceptor(httpLoggingInterceptor)
            .addNetworkInterceptor(networkInterceptor) // only used when network is on
            .addInterceptor(offlineCacheInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(serviceClass)
    }

    private fun cache(): Cache {
        val cacheSize = 5 * 1024 * 1024.toLong()
        return Cache(context.cacheDir, cacheSize)
    }
}