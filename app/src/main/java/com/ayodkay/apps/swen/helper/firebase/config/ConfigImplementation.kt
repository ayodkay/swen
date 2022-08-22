package com.ayodkay.apps.swen.helper.firebase.config

import com.ayodkay.apps.swen.helper.firebase.FirebaseInterface
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ConfigImplementation : ConfigInterface, KoinComponent {
    private val firebaseInterface: FirebaseInterface by inject()

    override fun getInstance(): FirebaseRemoteConfig {
        return firebaseInterface.remoteConfig
    }

    override fun setConfigSettingsAsync(settings: FirebaseRemoteConfigSettings) {
        firebaseInterface.remoteConfig.setConfigSettingsAsync(settings)
    }

    override fun setDefaultsAsync(resourceId: Int) {
        firebaseInterface.remoteConfig.setDefaultsAsync(resourceId)
    }

    override fun fetchAndActivate(): Task<Boolean> {
        return firebaseInterface.remoteConfig.fetchAndActivate()
    }
}
