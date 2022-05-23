package com.ayodkay.apps.swen.view.webview

import androidx.databinding.BindingAdapter

@BindingAdapter("app:loadUrl")
fun WebViewSuite.loadUrl(link: String) {
    startLoading(link)
}

@BindingAdapter("app:reload")
fun WebViewSuite.reload(reload: Boolean) {
    if (reload) {
        refresh()
    }
}
