package com.ayodkay.apps.swen.helper.mixpanel

import android.content.Context
import com.ayodkay.apps.swen.BuildConfig
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.firebase.FirebaseInterface
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.onesignal.OneSignal
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MixpanelImplementation : MixPanelInterface, KoinComponent {
    private val firebaseInterface: FirebaseInterface by inject()
    private var mixpanel: MixpanelAPI? = null

    override val debugKey: String
        get() = "b1b81dbf69056d59bbe7d8ef9d486bb2"

    override val productionKey: String
        get() = firebaseInterface.remoteConfig.getString("productionKeyMixPanel")

    override fun setMixpanelIdInOneSignal() {
        OneSignal.setExternalUserId(mixpanel?.distinctId.orEmpty())
    }

    override fun initialize(context: Context) {
        mixpanel =
            MixpanelAPI.getInstance(
                context,
                if (BuildConfig.DEBUG || Helper.isEmulator()) debugKey else productionKey
            )
    }

    override fun track(eventName: String) {
        mixpanel?.track(eventName)
    }

    override fun track(eventName: String, props: JSONObject) {
        mixpanel?.track(eventName, props)
    }
}
