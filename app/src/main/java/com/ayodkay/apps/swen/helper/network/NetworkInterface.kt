package com.ayodkay.apps.swen.helper.network

import kotlinx.coroutines.flow.StateFlow

interface NetworkInterface {
    fun netWorkConnected(): StateFlow<Boolean>
}
