package com.ayodkay.apps.swen.view.settings

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ayodkay.apps.swen.BuildConfig
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.room.country.Country
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.review.ReviewManagerFactory

class SettingsFragment : PreferenceFragmentCompat() {
    private val settingsViewModel: SettingsViewModel by viewModels()
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        var checkedSort = settingsViewModel.getSelectedCountryDao.countryDao().getAll().position!!
        var language = settingsViewModel.availableLanguages[checkedSort]

        val selected = isAvailable(
            settingsViewModel.availableLanguages,
            settingsViewModel.getSelectedCountryDao.countryDao().getAll().iso
        )

        if (selected > -1) {
            language = settingsViewModel.availableLanguages[selected]
            checkedSort = selected
        }

        findPreference<Preference?>("rate")?.let {
            it.setOnPreferenceClickListener {
                settingsViewModel.mixpanel.track("Rate App")
                goToPlayStore(requireContext())
                true
            }
        }
        findPreference<Preference?>("color")?.let {
            it.setOnPreferenceClickListener {
                findNavController()
                    .navigate(SettingsFragmentDirections.actionNavSettingsToNavThemeFragment())
                true
            }
        }
        findPreference<Preference?>("share")?.let {
            it.setOnPreferenceClickListener {
                settingsViewModel.mixpanel.track("Share App")
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "${resources.getString(R.string.share_app)}\n${
                        resources.getString(
                            R.string.google_play
                        )
                        }"
                    )
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)

                true
            }
        }
        findPreference<Preference?>("update")?.let {
            if (settingsViewModel.remoteConfig.getInstance().getString("versionCode") !=
                BuildConfig.VERSION_CODE.toString()
            ) {
                with(it) {
                    isVisible = true
                    setOnPreferenceClickListener {
                        goToPlayStore(requireContext())
                        true
                    }
                }
            }
        }
        findPreference<Preference?>("search")?.let {
            it.setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.search_language))
                    .setNeutralButton(resources.getString(android.R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, _ ->
                        with(settingsViewModel.getSelectedCountryDao.countryDao()) {
                            val countryName = getAll().country
                            delete()
                            insertAll(Country(countryName, language, checkedSort))
                        }
                        dialog.dismiss()
                    }
                    // Single-choice items (initialized with checked item)
                    .setSingleChoiceItems(settingsViewModel.singleSort, checkedSort) { _, which ->
                        language = settingsViewModel.availableLanguages[which]
                        checkedSort = which
                    }
                    .show()
                true
            }
        }
        findPreference<Preference?>("policy")?.let {
            it.setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(this.requireContext())
                    .setTitle(resources.getString(R.string.disclaimer))
                    .setMessage(resources.getString(R.string.supporting_text))
                    .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                true
            }
        }
        findPreference<Preference?>("version")?.let {
            it.summary = "v${BuildConfig.VERSION_NAME}"
        }
        findPreference<Preference?>("country")?.let {
            it.setOnPreferenceClickListener {
                findNavController()
                    .navigate(SettingsFragmentDirections.actionNavSettingsToNavLocation())
                true
            }
        }
        findPreference<EditTextPreference?>("feedback")?.let {
            it.setOnPreferenceChangeListener { _, newValue ->
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "message/rfc822"
                intent.putExtra(Intent.EXTRA_TEXT, newValue.toString())
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_suggestions))
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("ayodelekayode51@yahoo.com"))
                val mailer = Intent.createChooser(intent, null)
                startActivity(mailer)
                true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.background))
        return view
    }

    private fun goToPlayStore(context: Context?) {
        val uri: Uri = Uri.parse("market://details?id=" + context?.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)

        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            context?.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            context?.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
                )
            )
        }
    }

    private fun inAppRate(context: Context, activity: Activity) {
        val manager = ReviewManagerFactory.create(context)
        manager.requestReviewFlow()
            .addOnCompleteListener { requestReviewFlow ->
                if (requestReviewFlow.isSuccessful) {
                    manager.launchReviewFlow(activity, requestReviewFlow.result)
                        .addOnCompleteListener {}
                }
            }.addOnFailureListener {}
    }

    private fun isAvailable(list: ArrayList<String>, keyword: String): Int {
        var position = 0
        for (i in list) {
            if (keyword == i) {
                return position
            }
            position += 1
        }

        return -1
    }
}
