package com.ayodkay.apps.swen.helper

import android.util.Log
import androidx.multidex.BuildConfig

object AppLog {
    @JvmStatic
    fun l(message: Any) {
        if (BuildConfig.DEBUG) {
            Log.d("log", "AppLog: $message")
        }
    }
}