package com.ayodkay.apps.swen.notification

import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.AppLog
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {}

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        AppLog.log(message = remoteMessage.data)
        Notification(this)
            .sendNotification(getString(R.string.news_update))
    }
}