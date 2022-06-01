package com.ayodkay.apps.swen.view.splash

import android.app.Application
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.BaseViewModel
import com.ayodkay.apps.swen.helper.SimpleEvent
import com.ayodkay.apps.swen.helper.trigger
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus

class SplashViewModel(application: Application) : BaseViewModel(application) {
    val navigateEvent = SimpleEvent()

    val isUpdating = ObservableBoolean(false)
    val appUpdateManager: AppUpdateManager =
        AppUpdateManagerFactory.create(application.applicationContext)

    val listener: InstallStateUpdatedListener =
        InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                isUpdating.set(false)
                Toast.makeText(
                    application.applicationContext,
                    application.applicationContext.getString(R.string.updated),
                    Toast.LENGTH_LONG
                ).show()
                navigateEvent.trigger()
            }

            if (installState.installStatus() == InstallStatus.DOWNLOADING) {
                isUpdating.set(true)
                Toast.makeText(
                    application.applicationContext,
                    application.applicationContext.getString(R.string.updating),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
}
