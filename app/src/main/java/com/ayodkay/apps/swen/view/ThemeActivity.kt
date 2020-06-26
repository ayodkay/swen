package com.ayodkay.apps.swen.view

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.ayodkay.apps.swen.R
import kotlinx.android.synthetic.main.activity_theme.*


const val PREFS_NAME = "theme_prefs"
const val KEY_THEME = "prefs.theme"
const val THEME_UNDEFINED = -1
const val THEME_LIGHT = 0
const val THEME_DARK = 1


class ThemeActivity : AppCompatActivity() {
    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }


    override fun onStart() {
        super.onStart()

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {

            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                background.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            } // Night mode is active, we're using dark theme
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme)

        initThemeListener()
        initTheme()
    }

    private fun initThemeListener(){
        themeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.themeLight -> setTheme(AppCompatDelegate.MODE_NIGHT_NO, THEME_LIGHT)
                R.id.themeDark -> setTheme(AppCompatDelegate.MODE_NIGHT_YES, THEME_DARK)
            }
        }
    }

    private fun setTheme(themeMode: Int, prefsMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
        saveTheme(prefsMode)
    }

    private fun initTheme() {
        when (getSavedTheme()) {
            THEME_LIGHT -> themeLight.isChecked = true
            THEME_DARK -> themeDark.isChecked = true
            THEME_UNDEFINED -> {
                when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_NO -> themeLight.isChecked = true
                    Configuration.UI_MODE_NIGHT_YES -> themeDark.isChecked = true
                    Configuration.UI_MODE_NIGHT_UNDEFINED -> themeLight.isChecked = true
                }
            }
        }
    }

    private fun saveTheme(theme: Int) = sharedPrefs.edit().putInt(KEY_THEME, theme).apply()

    private fun getSavedTheme() = sharedPrefs.getInt(KEY_THEME, THEME_UNDEFINED)
}

