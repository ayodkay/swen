package com.ayodkay.apps.swen.helper.extentions

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("app:contentText", "app:descriptionText")
fun TextView.contentText(content: String, description: String) {
    this.text = content.ifBlank { description }
        .replace(regex = Regex("<.*?>"), "")
        .replace(regex = Regex("\\W+\\d+ chars\\W"), "...")
        .trim()
}
