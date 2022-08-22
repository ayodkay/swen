package com.ayodkay.apps.swen.view.location

import android.app.Application
import com.ayodkay.apps.swen.view.BaseViewModel
import com.ayodkay.apps.swen.view.SimpleEvent
import com.ayodkay.apps.swen.view.trigger

class LocationViewModel(application: Application) : BaseViewModel(application) {
    val showDialogEvent = SimpleEvent()
    fun showDialog() = showDialogEvent.trigger()
}
