package com.ayodkay.apps.swen.view.location

import android.app.Application
import com.ayodkay.apps.swen.helper.BaseViewModel
import com.ayodkay.apps.swen.helper.SimpleEvent
import com.ayodkay.apps.swen.helper.trigger

class LocationViewModel(application: Application) : BaseViewModel(application) {
    val showDialogEvent = SimpleEvent()
    fun showDialog() = showDialogEvent.trigger()
}
