package com.ayodkay.apps.swen.view.settings

import android.app.Application
import com.ayodkay.apps.swen.view.BaseViewModel

class SettingsViewModel(application: Application) : BaseViewModel(application) {
    val availableLanguages = arrayListOf(
        "ar", "de", "en", "es", "fr", "it", "nl", "no", "pt", "ru", "se", "zh"
    )

    val singleSort = arrayOf(
        "Arabic", "German", "English", "Spanish", "French", "Italian", "Dutch",
        "Norwegian", "Portuguese", "Russian", "Swedish", "Chinese"
    )
}
