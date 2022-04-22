package com.ayodkay.apps.swen.helper.extentions

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("app:isVisible")
fun View.isVisible(value: Boolean) =
    if (value) this.visibility = View.VISIBLE else this.visibility = View.GONE