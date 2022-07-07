package com.ayodkay.apps.swen.view.main

import android.app.Application
import androidx.work.Data
import com.ayodkay.apps.swen.App
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.BaseViewModel
import com.ayodkay.apps.swen.helper.work.NotifyWork
import com.google.android.gms.location.LocationServices

class MainActivityViewModel(application: Application) : BaseViewModel(application) {
    val data = Data.Builder().putInt(NotifyWork.NOTIFICATION_ID, 0).build()

    init {
        App.scheduleNotification(data, application.applicationContext)
    }

    var fusedLocationClient = LocationServices
        .getFusedLocationProviderClient(application.applicationContext)

    fun bottomBarIds(): ArrayList<Int> {
        return arrayListOf(
            R.id.nav_main_swen, R.id.navigation_bookmarks, R.id.nav_main_search,
            R.id.nav_main_links, R.id.nav_settings
        )
    }
}
