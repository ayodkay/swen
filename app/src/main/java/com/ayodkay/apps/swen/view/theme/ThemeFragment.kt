package com.ayodkay.apps.swen.view.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ayodkay.apps.swen.databinding.ThemeFragmentBinding
import com.ayodkay.apps.swen.helper.BaseFragment

const val PREFS_NAME = "theme_prefs"
const val KEY_THEME = "prefs.theme"
const val THEME_UNDEFINED = -1
const val THEME_LIGHT = 0
const val THEME_DARK = 1

class ThemeFragment : BaseFragment() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ThemeFragmentBinding.inflate(inflater, container, false).apply {
        viewModel = themeViewModel
        themeViewModel.initTheme()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed { navigateUp() }
        themeViewModel.loadAd.set(true)
    }
}
