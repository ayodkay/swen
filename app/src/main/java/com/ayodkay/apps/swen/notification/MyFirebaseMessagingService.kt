package com.ayodkay.apps.swen.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {}
    override fun onMessageReceived(remoteMessage: RemoteMessage) {}
}
