package com.ayodkay.apps.swen.helper.extentions

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("app:isVisible")
fun View.isVisible(value: Boolean) {
    visibility = if (value) View.VISIBLE else View.GONE
}
