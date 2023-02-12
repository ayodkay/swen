package com.ayodkay.apps.swen.helper.firebase.config

import androidx.annotation.XmlRes
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

interface ConfigInterface {
    fun getInstance(): FirebaseRemoteConfig
    fun setConfigSettingsAsync(settings: FirebaseRemoteConfigSettings)
    fun setDefaultsAsync(@XmlRes resourceId: Int)
    fun fetchAndActivate(): Task<Boolean>
}
