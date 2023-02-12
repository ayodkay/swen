package com.ayodkay.apps.swen.view.main

import android.app.Application
import androidx.lifecycle.asLiveData
import androidx.work.Data
import com.ayodkay.apps.swen.App
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.extentions.PreferenceExtension
import com.ayodkay.apps.swen.helper.network.NetworkInterface
import com.ayodkay.apps.swen.helper.work.NotifyWork
import com.ayodkay.apps.swen.view.BaseViewModel
import com.google.android.gms.location.LocationServices
import org.koin.core.component.inject

class MainActivityViewModel(application: Application) : BaseViewModel(application) {
    private val networkInterface: NetworkInterface by inject()
    val isNetworkConnected = networkInterface.netWorkConnected().asLiveData()
    val data = Data.Builder().putInt(NotifyWork.NOTIFICATION_ID, 0).build()
    var prevPosition: Int by PreferenceExtension(application.applicationContext, "prevPosition", 0)

    init {
        prevPosition = 0
        App.scheduleNotification(data, application.applicationContext)
    }

    var fusedLocationClient = LocationServices
        .getFusedLocationProviderClient(application.applicationContext)

    fun bottomBarIds(): ArrayList<Int> {
        return arrayListOf(
            R.id.nav_main_swen,
            R.id.navigation_bookmarks,
            R.id.nav_main_search,
            R.id.nav_main_links,
            R.id.nav_settings
        )
    }
}
