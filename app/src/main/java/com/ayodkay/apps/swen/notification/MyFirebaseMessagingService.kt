package com.ayodkay.apps.swen.notification

import com.ayodkay.apps.swen.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {}

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        when {
            remoteMessage.data["isEngage"].toBoolean() -> {
                Notification(this)
                    .sendEngageNotification(getString(R.string.news_update))
            }
            remoteMessage.data["update"].toBoolean() -> {
                Notification(this)
                    .sendUpdateNotification(getString(R.string.update_available))
            }
            else -> {
                Notification(this).sendCountryNotification(
                    remoteMessage.data["title"]!!,
                    remoteMessage.data["description"]!!, remoteMessage.data["url"]!!,
                    remoteMessage.data["image"]!!
                )
            }
        }
    }
}