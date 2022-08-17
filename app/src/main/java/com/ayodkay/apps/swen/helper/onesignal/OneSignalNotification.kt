package com.ayodkay.apps.swen.helper.onesignal

data class OneSignalNotification(
    val group: String,
    val title: String,
    val message: String,
    val smallIconRes: String,
    val largeIconUrl: String,
    val bigPictureUrl: String,
    val url: String,
    val iconUrl: String,
    val buttons: String,
    val shouldShow: Boolean
)
