package com.ayodkay.apps.swen.helper.backend

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.notification.Notification
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class BootReceiver :BroadcastReceiver() {
    private var TAG: String = BootReceiver::class.java.name
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val appUpdateManager: AppUpdateManager? = AppUpdateManagerFactory.create(context)
            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, NotificationReceiver::class.java).let { notify_intent ->
                notify_intent.putExtra("swen-notify", "notify")
                PendingIntent.getBroadcast(context, 0, notify_intent, 0)
            }

            alarmMgr.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                1000 * 60 * 240,
                alarmIntent
            )
            // Returns an intent object that you use to check for an update.
            val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
            // Checks that the platform will allow the specified type of update.
            appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    Notification(context).sendEngageNotification(context.getString(R.string.update_available))
                }
            }
        }

    }
}