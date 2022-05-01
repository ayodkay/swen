package com.ayodkay.apps.swen.view.viewimage

import androidx.databinding.ObservableField
import com.ayodkay.apps.swen.helper.BaseViewModel

class ViewImageViewModel : BaseViewModel() {
    val pinchIsVisible = ObservableField(true)
    fun hidePInch() {
        pinchIsVisible.set(false)
    }
}