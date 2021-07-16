package com.ayodkay.apps.swen.helper.work

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ayodkay.apps.swen.R
import com.facebook.appevents.AppEventsLogger
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability


private const val MY_REQUEST_CODE = 1

class UpdateWorkManager(
    private val appContext: Context,
    workerParams: WorkerParameters
) :
    Worker(appContext, workerParams) {
    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(appContext)
    private var listener: InstallStateUpdatedListener? = null

    override fun doWork(): Result {
        listener =
            InstallStateUpdatedListener { installState ->
                if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                    //binding.updating.visibility = View.GONE
                    Toast.makeText(
                        appContext,
                        appContext.getString(R.string.updated),
                        Toast.LENGTH_LONG
                    ).show()
                }

                if (installState.installStatus() == InstallStatus.DOWNLOADING) {
                    //binding.updating.visibility = View.VISIBLE
                    Toast.makeText(
                        appContext,
                        appContext.getString(R.string.updating),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        // Do the work here--in this case, upload the images.
        uploadImages()

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }


    private fun uploadImages() {
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {

                AppEventsLogger.newLogger(appContext).logEvent("in-appUpdate")

                appUpdateManager.registerListener(listener!!)
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    AppUpdateType.FLEXIBLE,
                    // The current activity making the update request.
                    appContext as Activity,
                    // Include a request code to later monitor this update request.
                    MY_REQUEST_CODE
                )
            } else {
                appUpdateManager.unregisterListener(listener!!)
                Result.success()
            }
        }.addOnFailureListener {
            appUpdateManager.unregisterListener(listener!!)
            Result.failure()
        }


    }
}