package com.ayodkay.apps.swen.view.theme

import android.widget.RadioButton
import androidx.databinding.BindingAdapter

@BindingAdapter("check")
fun RadioButton.check(check: Boolean) {
    isChecked = check
}
