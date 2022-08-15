package com.ayodkay.apps.swen.helper.backend

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import com.ayodkay.apps.swen.App.Companion.scheduleNotification
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.onesignal.OneSignalNotification
import com.ayodkay.apps.swen.helper.onesignal.OneSignalNotificationSender
import com.ayodkay.apps.swen.helper.work.NotifyWork
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class PowerButtonBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SCREEN_OFF || intent.action == Intent.ACTION_SCREEN_ON) {
            val data = Data.Builder().putInt(NotifyWork.NOTIFICATION_ID, 0).build()
            scheduleNotification(data, context)

            val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)
            // Returns an intent object that you use to check for an update.
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            // Checks that the platform will allow the specified type of update.
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                ) {
                    OneSignalNotificationSender
                        .sendDeviceNotification(
                            OneSignalNotification(
                                "Update App", context.getString(R.string.update_available),
                                "\u200E", "ic_stat_onesignal_default.png",
                                context.getString(R.string.notification_icon),
                                context.getString(R.string.ic_logo),
                                "", "", "[]", true
                            ),
                            context
                        )
                }
            }
        }
    }
}
