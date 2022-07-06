package com.ayodkay.apps.swen.helper.onesignal

import android.content.Context
import android.content.Intent
import com.ayodkay.apps.swen.BuildConfig
import com.ayodkay.apps.swen.helper.extentions.isNotNull
import com.ayodkay.apps.swen.helper.mixpanel.MixPanelInterface
import com.ayodkay.apps.swen.view.main.MainActivity
import com.onesignal.OSDeviceState
import com.onesignal.OneSignal
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class OneSignalImplementation(private val mixpanel: MixPanelInterface) :
    OneSignalInterface, KoinComponent {
    private val context: Context by inject()

    override val productionKey: String
        get() = "c029b873-28be-4fa7-9d59-111ea9682596"

    override val debugKey: String
        get() = "1b294a36-a306-4117-8d5e-393ee674419d"

    override val basic: String
        get() = "key"

    override fun initialize(context: Context) {
        OneSignal.setRequiresUserPrivacyConsent(true)
        OneSignal.initWithContext(context)
        OneSignal.setAppId(if (BuildConfig.DEBUG) debugKey else productionKey)
        OneSignal.provideUserConsent(true)
        mixpanel.setMixpanelIdInOneSignal()
    }

    override fun setExternalId(userId: String) {
        OneSignal.setExternalUserId(userId)
    }

    override fun clearNotification() {
        OneSignal.clearOneSignalNotifications()
    }

    override fun deviceState(): OSDeviceState? {
        return OneSignal.getDeviceState()
    }

    override fun setNotificationOpenedHandler() {
        OneSignal.setNotificationOpenedHandler { result ->
            val notification = result.notification
            val data = notification.additionalData
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.putExtra("url", if (data.isNotNull()) data.optString("url") else "")
                .putExtra("isPush", true)
                .putExtra("toMain", true)
            val props = JSONObject().apply {
                put("source", "setNotificationOpenedHandler")
                put("notification", notification.title)
                put("state", "opened")
            }
            mixpanel.track("Push Notification", props)
            context.startActivity(intent)
        }
    }

    override fun setNotificationWillShowInForegroundHandler() {
        OneSignal.setNotificationWillShowInForegroundHandler { notificationReceivedEvent ->
            val notification = notificationReceivedEvent.notification
            val props = JSONObject().apply {
                put("source", "setNotificationWillShowInForegroundHandler")
                put("notification", notification.title)
                put("state", "received")
            }
            mixpanel.track("Push Notification", props)
            notificationReceivedEvent.complete(notification)
        }
    }
}
