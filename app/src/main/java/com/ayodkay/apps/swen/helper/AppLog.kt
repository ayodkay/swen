package com.ayodkay.apps.swen.helper

import android.util.Log
import com.ayodkay.apps.swen.BuildConfig

object AppLog {
    @JvmStatic
    fun l(message: Any) {
        if (BuildConfig.DEBUG) {
            Log.d("log", "AppLog: $message")
        }
    }
}