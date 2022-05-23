package com.ayodkay.apps.swen.view.viewimage

import android.app.Application
import androidx.databinding.ObservableField
import com.ayodkay.apps.swen.helper.BaseViewModel

class ViewImageViewModel(application: Application) : BaseViewModel(application) {
    val pinchIsVisible = ObservableField(true)
    fun hidePInch() {
        pinchIsVisible.set(false)
    }
}
