package com.ayodkay.apps.swen.helper

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatDelegate
import com.ayodkay.apps.swen.helper.backend.BootReceiver
import com.ayodkay.apps.swen.helper.backend.PowerButtonBroadcastReceiver
import com.ayodkay.apps.swen.view.KEY_THEME
import com.ayodkay.apps.swen.view.PREFS_NAME
import com.ayodkay.apps.swen.view.THEME_UNDEFINED
import com.google.firebase.messaging.FirebaseMessaging


class App : Application(){

    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
        when(sharedPrefs.getInt(KEY_THEME, THEME_UNDEFINED)){
            1->{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            0->{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val receiver = ComponentName(context, BootReceiver::class.java)

        context.packageManager.setComponentEnabledSetting(
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

    companion object{
        lateinit var context:Context
    }
}