package com.ayodkay.apps.swen.view.theme

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.ObservableBoolean
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.BaseViewModel

class ThemeViewModel(application: Application) : BaseViewModel(application) {
    private val sharedPrefs by lazy {
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    val themeLight = ObservableBoolean(false)
    val themeDark = ObservableBoolean(false)
    val themeDefault = ObservableBoolean(false)

    fun checkedChangeListener(id: Int) {
        when (id) {
            R.id.themeLight -> setTheme(AppCompatDelegate.MODE_NIGHT_NO, THEME_LIGHT)
            R.id.themeDark -> setTheme(AppCompatDelegate.MODE_NIGHT_YES, THEME_DARK)
            R.id.themeSystem -> setTheme(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                THEME_UNDEFINED
            )
        }
    }

    fun initTheme() {
        when (getSavedTheme()) {
            THEME_LIGHT -> {
                themeLight.set(true)
                themeDark.set(false)
                themeDefault.set(false)
            }
            THEME_DARK -> {
                themeDark.set(true)
                themeLight.set(false)
                themeDefault.set(false)
            }
            THEME_UNDEFINED -> {
                themeDefault.set(true)
                themeLight.set(false)
                themeDark.set(false)
            }
        }
    }

    private fun setTheme(themeMode: Int, prefsMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
        saveTheme(prefsMode)
    }

    private fun saveTheme(theme: Int) =
        sharedPrefs.edit().putInt(KEY_THEME, theme).apply()

    private fun getSavedTheme() =
        sharedPrefs.getInt(KEY_THEME, THEME_UNDEFINED)
}
