package com.ayodkay.apps.swen.view.viewimage

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

class ViewImageViewModel : ViewModel() {
    val pinchIsVisible = ObservableField(true)
    val imageUrl = ObservableField("")
    val loadAd = ObservableField(true)
}