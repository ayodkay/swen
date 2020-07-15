package com.ayodkay.apps.swen.helper

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
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
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
    }

    companion object{
        lateinit var context:Context
    }
}