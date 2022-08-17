package com.ayodkay.apps.swen.helper.firebase

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig

class FirebaseImplementation : FirebaseInterface {
    override val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
}
