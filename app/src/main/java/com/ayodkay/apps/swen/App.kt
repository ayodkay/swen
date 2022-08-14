package com.ayodkay.apps.swen

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.applovin.sdk.AppLovinSdk
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.backend.BootReceiver
import com.ayodkay.apps.swen.helper.backend.PowerButtonBroadcastReceiver
import com.ayodkay.apps.swen.helper.di.appModule
import com.ayodkay.apps.swen.helper.mixpanel.MixPanelInterface
import com.ayodkay.apps.swen.helper.onesignal.OneSignalInterface
import com.ayodkay.apps.swen.helper.work.NotifyWork
import com.ayodkay.apps.swen.view.theme.KEY_THEME
import com.ayodkay.apps.swen.view.theme.PREFS_NAME
import com.ayodkay.apps.swen.view.theme.THEME_UNDEFINED
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.TimeUnit
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    private val oneSignal: OneSignalInterface by inject()
    private val mixpanel: MixPanelInterface by inject()
    private val sharedPrefs by lazy { getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(appModule))
        }
        mixpanel.initialize(this)
        oneSignal.initialize(this)
        oneSignal.setNotificationOpenedHandler()
        oneSignal.setNotificationWillShowInForegroundHandler()

        AppLovinSdk.getInstance(this).apply {
            mediationProvider = "max"
            initializeSdk {}
        }

        if (BuildConfig.DEBUG || Helper.isEmulator()) {
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
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        val mReceiver = PowerButtonBroadcastReceiver()
        registerReceiver(mReceiver, filter)
    }

    companion object {
        fun scheduleNotification(data: Data, context: Context) {
            val nWorkerParameters =
                PeriodicWorkRequest.Builder(
                    NotifyWork::class.java,
                    4,
                    TimeUnit.HOURS,
                    15,
                    TimeUnit.MINUTES
                ).apply {
                    setInitialDelay(30, TimeUnit.MINUTES)
                    setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.HOURS)
                    setInputData(data)
                }.build()

            WorkManager.getInstance(context).apply {
                enqueueUniquePeriodicWork(
                    NotifyWork.NOTIFICATION_WORK,
                    ExistingPeriodicWorkPolicy.KEEP,
                    nWorkerParameters
                )
            }
        }
    }
}
