package com.ayodkay.apps.swen.helper.backend

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.notification.Notification

class NotificationReceiver :BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.getStringExtra("swen-notify") != null &&
            intent.getStringExtra("swen-notify") == "notify"
        ){
            Notification(context).sendEngageNotification(context.getString(R.string.news_update))
        }
    }
}