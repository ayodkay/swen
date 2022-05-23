package com.ayodkay.apps.swen.view.webview

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.ayodkay.apps.swen.helper.BaseViewModel
import com.ayodkay.apps.swen.helper.SimpleEvent
import com.ayodkay.apps.swen.helper.trigger

class WebViewViewModel(application: Application) : BaseViewModel(application) {
    var bookmarkDrawable = ObservableInt()
    val openBrowser = SimpleEvent()
    val refreshPage = SimpleEvent()
    val shareUrl = SimpleEvent()
    val bookmarkUrl = SimpleEvent()
    val gotBack = SimpleEvent()

    var webLink = ObservableField("")
    var webDomain = ObservableField("")
    var navigateToMain = false

    fun refreshTrigger() {
        refreshPage.trigger()
    }

    fun openBrowserTrigger() {
        openBrowser.trigger()
    }

    fun shareUrlTrigger() {
        shareUrl.trigger()
    }

    fun updateBookmarkTrigger() {
        bookmarkUrl.trigger()
    }

    fun backPressTrigger() {
        gotBack.trigger()
    }
}
