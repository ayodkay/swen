package com.ayodkay.apps.swen.helper.onesignal

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.ayodkay.apps.swen.BuildConfig
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.BaseViewModel
import com.ayodkay.apps.swen.helper.mixpanel.MixPanelInterface
import com.google.gson.Gson
import com.onesignal.OneSignal
import com.onesignal.OneSignal.PostNotificationResponseHandler
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import org.json.JSONException
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object OneSignalNotificationSender : KoinComponent {
    private val mixpanel: MixPanelInterface by inject()
    private val oneSignal: OneSignalInterface by inject()
    private val baseViewModel: BaseViewModel by inject()
    fun sendDeviceNotification(notification: Notification, context: Context) {
        val ledColor = ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null)
        Thread(
            Runnable {
                val deviceState = oneSignal.deviceState()
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
                                val props = JSONObject().apply {
                                    put("source", "sendDeviceNotification")
                                    put("sender", oneSignal.deviceState()?.userId)
                                    put("state", "sent")
                                }
                                mixpanel.track("Push Notification", props)
                            }

                            override fun onFailure(response: JSONObject) {
                                val props = JSONObject().apply {
                                    put("source", "sendDeviceNotification")
                                    put("reason", "Failure sending notification: $response")
                                }
                                mixpanel.track("onFailure", props)
                            }
                        }
                    )
                } catch (e: JSONException) {
                    val props = JSONObject().apply {
                        put("source", "sendDeviceNotification")
                        put("reason", "Failure sending notification: ${e.printStackTrace()}")
                    }
                    mixpanel.track("onFailure", props)
                }
            }
        ).start()
    }

    fun sendDeviceNotificationWithRequest(notification: Notification, context: Context) {
        val ledColor = ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null)
        Thread(
            Runnable {
                val pos: Int = notification.templatePos
                val map: MutableMap<String?, Any?> = HashMap()
                val filter = arrayListOf(
                    mapOf(
                        Pair("field", "tag"), Pair("key", "country"),
                        Pair("relation", "="),
                        Pair("value", baseViewModel.getSelectedLocationDao.getAll().countryCode)
                    )
                )
                map["app_id"] =
                    if (BuildConfig.DEBUG) oneSignal.debugKey else oneSignal.productionKey
                map["filters"] = filter
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
                try {
                    val url = URL("https://onesignal.com/api/v1/notifications")
                    val con: HttpURLConnection = url.openConnection() as HttpURLConnection
                    con.useCaches = false
                    con.doOutput = true
                    con.doInput = true
                    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    con.setRequestProperty(
                        "Authorization", "Basic ${oneSignal.basic}"
                    )
                    con.requestMethod = "POST"
                    val strJsonBody = Gson().toJson(map)
                    val sendBytes = strJsonBody.toByteArray(charset("UTF-8"))
                    con.setFixedLengthStreamingMode(sendBytes.size)
                    val outputStream: OutputStream = con.outputStream
                    outputStream.write(sendBytes)
                    val httpResponse: Int = con.responseCode
                    if (httpResponse >= HttpURLConnection.HTTP_OK &&
                        httpResponse < HttpURLConnection.HTTP_BAD_REQUEST
                    ) {
                        val scanner = Scanner(con.inputStream, "UTF-8")
                        val props = JSONObject().apply {
                            put("source", "sendDeviceNotificationWithRequest")
                            put("sender", oneSignal.deviceState()?.userId)
                            put("state", "sent")
                        }
                        mixpanel.track("Push Notification", props)
                        scanner.close()
                    } else {
                        val scanner = Scanner(con.errorStream, "UTF-8")
                        val props = JSONObject().apply {
                            put("source", "sendDeviceNotificationWithRequest")
                            put("reason", "Failure sending notification: ${scanner.next()}")
                        }
                        mixpanel.track("onFailure", props)
                        scanner.close()
                        sendDeviceNotification(notification, context)
                    }
                } catch (t: Throwable) {
                    val props = JSONObject()
                    props.put("source", "sendDeviceNotificationWithRequest")
                    props.put("reason", "Failure sending notification: $t")
                    mixpanel.track("onFailure", props)
                    sendDeviceNotification(notification, context)
                }
            }
        ).start()
    }
}
