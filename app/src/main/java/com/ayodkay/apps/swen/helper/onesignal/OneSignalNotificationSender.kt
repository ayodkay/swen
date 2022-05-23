package com.ayodkay.apps.swen.helper.onesignal

import android.content.Context
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import com.ayodkay.apps.swen.R
import com.onesignal.OneSignal
import com.onesignal.OneSignal.PostNotificationResponseHandler
import org.json.JSONException
import org.json.JSONObject

object OneSignalNotificationSender {
    fun sendDeviceNotification(notification: Notification, context: Context) {
        val ledColor = ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null)
        Thread(
            Runnable {
                val deviceState = OneSignal.getDeviceState()
                val userId = deviceState?.userId
                val isSubscribed = deviceState != null && deviceState.isSubscribed
                if (!isSubscribed) return@Runnable
                val pos: Int = notification.templatePos
                try {
                    val map: MutableMap<String?, Any?> = HashMap()
                    map["include_player_ids"] = arrayOf(userId)
                    map["headings"] = mapOf(Pair("en", notification.getTitle(pos)))
                    map["contents"] = mapOf(Pair("en", notification.getMessage(pos)))
                    map["small_icon"] = notification.smallIconRes
                    map["large_icon"] = notification.getLargeIconUrl(pos)
                    map["big_picture"] = notification.getBigPictureUrl(pos)
                    map["android_group"] = notification.group
                    map["buttons"] = emptyArray<String>()
                    map["android_led_color"] = "$ledColor"
                    map["android_accent_color"] = "$ledColor"
                    map["data"] = mapOf(Pair("url", notification.getUrl(pos)))
                    map["android_sound"] = "nil"
                    val json = JSONObject(map)
                    OneSignal.postNotification(
                        json,
                        object : PostNotificationResponseHandler {
                            override fun onSuccess(response: JSONObject) {
                                Log.d(
                                    "DEBUG",
                                    "Success sending notification: $response"
                                )
                            }

                            override fun onFailure(response: JSONObject) {
                                Log.d(
                                    "ERROR",
                                    "Failure sending notification: $response"
                                )
                            }
                        }
                    )
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        ).start()
    }
}
