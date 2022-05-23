package com.ayodkay.apps.swen.helper

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatDelegate
import com.applovin.sdk.AppLovinSdk
import com.ayodkay.apps.swen.BuildConfig
import com.ayodkay.apps.swen.helper.backend.BootReceiver
import com.ayodkay.apps.swen.helper.backend.PowerButtonBroadcastReceiver
import com.ayodkay.apps.swen.helper.extentions.ifNull
import com.ayodkay.apps.swen.view.main.MainActivity
import com.ayodkay.apps.swen.view.theme.KEY_THEME
import com.ayodkay.apps.swen.view.theme.PREFS_NAME
import com.ayodkay.apps.swen.view.theme.THEME_UNDEFINED
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.onesignal.OneSignal

class App : Application() {

    private val sharedPrefs by lazy { getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    override fun onCreate() {
        super.onCreate()
        AppLovinSdk.getInstance(this).mediationProvider = "max"
        AppLovinSdk.getInstance(this).initializeSdk {}
        application = this
        if (BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        }
        when (sharedPrefs.getInt(KEY_THEME, THEME_UNDEFINED)) {
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val receiver = ComponentName(this, BootReceiver::class.java)

        packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId("1b294a36-a306-4117-8d5e-393ee674419d")

        OneSignal.setNotificationOpenedHandler { result ->
            val notification = result.notification
            val data = notification.additionalData
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.putExtra("url", data["url"].ifNull { "" } as String)
                .putExtra("isPush", true)
                .putExtra("toMain", true)
            startActivity(intent)
            AppLog.l(data)
        }

        OneSignal.setNotificationWillShowInForegroundHandler { notificationReceivedEvent ->
            val notification = notificationReceivedEvent.notification
            val data = notification.additionalData
            notificationReceivedEvent.complete(notification)
        }
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        val mReceiver = PowerButtonBroadcastReceiver()
        registerReceiver(mReceiver, filter)
    }

    companion object {
        var application: Application? = null
            private set
        val context: Context
            get() = application!!.applicationContext
    }
}
