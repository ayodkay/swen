package com.ayodkay.apps.swen.helper.mixpanel

import android.content.Context
import org.json.JSONObject

interface MixPanelInterface {
    val productionKey: String
    val debugKey: String
    fun setMixpanelIdInOneSignal()
    fun initialize(context: Context)
    fun track(eventName: String)
    fun track(eventName: String, props: JSONObject)
}
