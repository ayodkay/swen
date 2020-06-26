package com.ayodkay.apps.swen.helper

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class App : Application(){

    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode())
    }


    companion object{
        lateinit var context:Context
    }
}