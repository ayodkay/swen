package com.ayodkay.apps.swen.helper

import android.util.Log
import com.ayodkay.apps.swen.BuildConfig

object AppLog {
    @JvmStatic
    fun log(tag: String= "tag", message: Any) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, "AppLog: $message")
        }
    }
}