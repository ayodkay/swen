package com.ayodkay.apps.swen.helper.onesignal

import android.content.Context
import com.onesignal.OSDeviceState

interface OneSignalInterface {
    val productionKey: String
    val debugKey: String
    val basic: String
    fun initialize(context: Context)
    fun setExternalId(userId: String)
    fun clearNotification()
    fun deviceState(): OSDeviceState?
    fun setNotificationOpenedHandler()
    fun setNotificationWillShowInForegroundHandler()
}
