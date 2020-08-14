package com.ayodkay.apps.swen.helper

import com.ayodkay.apps.swen.BuildConfig
import android.util.Log

object AppLog {
    @JvmStatic
    fun log(tag: String= "tag", message: Any) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, "AppLog: $message")
        }
    }
}