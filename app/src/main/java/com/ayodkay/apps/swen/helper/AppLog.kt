package com.ayodkay.apps.swen.helper

import android.util.Log
import com.ayodkay.apps.swen.BuildConfig.DEBUG

object AppLog {

    fun l(message: Any) {
        if (DEBUG) {
            Log.d("log", "AppLog: $message")
        }
    }
}
