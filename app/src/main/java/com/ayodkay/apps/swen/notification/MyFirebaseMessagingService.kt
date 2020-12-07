package com.ayodkay.apps.swen.notification

import com.ayodkay.apps.swen.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {}

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data["isEngage"].toBoolean()){
            Notification(this)
                .sendEngageNotification(getString(R.string.news_update))
        }else{
            Notification(this).sendCountryNotification(remoteMessage.data["title"]!!,
                    remoteMessage.data["description"]!!,remoteMessage.data["url"]!!,
                remoteMessage.data["image"]!!)
        }
    }
}