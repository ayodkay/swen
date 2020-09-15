package com.ayodkay.apps.swen.view.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.room.Room
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.room.country.Country
import com.ayodkay.apps.swen.view.AskLocation
import com.ayodkay.apps.swen.view.SaveNews
import com.ayodkay.apps.swen.view.ThemeActivity
import com.ayodkay.apps.swen.view.main.MainActivity
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.settings_activity.*
import kotlin.collections.ArrayList


class SettingsActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {

            }
            Configuration.UI_MODE_NIGHT_YES -> {
                setTheme(R.style.AppThemeNight)
                background.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private lateinit var mInterstitialAd: InterstitialAd

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val db = Helper.getCountryDatabase(this.requireContext())


            val availableLanguages = arrayListOf(
                "ar", "de", "en", "es", "fr", "it", "nl", "no", "pt", "ru", "se", "zh"
            )
            val singleSort = arrayOf(
                "Arabic", "German", "English", "Spanish", "French", "Italian", "Dutch",
                "Norwegian", "Portuguese", "Russian", "Swedish", "Chinese"
            )

            var language = ""

            var checkedSort = db.countryDao().getAll().position!!
            language = availableLanguages[checkedSort]

            val selected = isAvailable(availableLanguages,db.countryDao().getAll().iso)
            if (selected > -1){
                language = availableLanguages[selected]
                checkedSort = selected
            }

            val feedback: EditTextPreference? = findPreference("feedback")
            val saved: Preference? = findPreference("saved")
            val country: Preference? = findPreference("country")
            val share: Preference? = findPreference("share")
            val rate: Preference? = findPreference("rate")
            val color: Preference? = findPreference("color")
            val support: Preference? = findPreference("support")
            val search: Preference? = findPreference("search")

            setupAds(support)

            feedback?.setOnPreferenceChangeListener { _, newValue ->
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "message/rfc822"
                intent.putExtra(Intent.EXTRA_TEXT, newValue.toString())
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_suggestions))
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("ayodelekayode51@yahoo.com"))
                val mailer = Intent.createChooser(intent, null)
                startActivity(mailer)
                true
            }

            saved?.setOnPreferenceClickListener {
                startActivity(Intent(this.context, SaveNews::class.java))
                true
            }

            country?.setOnPreferenceClickListener {
                startActivity(Intent(this.context, AskLocation::class.java))
                true
            }

            share?.setOnPreferenceClickListener {
                AppEventsLogger.newLogger(context).logEvent("appShare")
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT, "${resources.getString(R.string.share_app)}\n${
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
            rate?.setOnPreferenceClickListener {
                AppEventsLogger.newLogger(context).logEvent("appRate")
                goToPlayStore(context)
                true
            }
            color?.setOnPreferenceClickListener {
                startActivity(Intent(this.context, ThemeActivity::class.java))
                true
            }
            search?.setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.search_language))
                    .setNeutralButton(resources.getString(android.R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, _ ->
                        val countryName = db.countryDao().getAll().country
                        db.countryDao().delete()
                        db.countryDao().insertAll(
                            Country(
                                countryName,
                                language,
                                checkedSort
                            )
                        )
                        dialog.dismiss()
                    }
                    // Single-choice items (initialized with checked item)
                    .setSingleChoiceItems(singleSort, checkedSort) { _, which ->
                        language = availableLanguages[which]
                        checkedSort = which
                    }
                    .show()
                true
            }
            support?.setOnPreferenceClickListener {
                AppEventsLogger.newLogger(context).logEvent("appSupport")
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                }
                true
            }
        }

        private fun setupAds(support: Preference?) {
            MobileAds.initialize(requireContext()) {}
            mInterstitialAd = InterstitialAd(requireContext())
            mInterstitialAd.adUnitId = resources.getString(R.string.interstitial_ad_unit_id)
            mInterstitialAd.loadAd(AdRequest.Builder().build())

            mInterstitialAd.adListener = object: AdListener() {
                override fun onAdLoaded() {
                    support?.isEnabled = true
                    support?.summary = resources.getString(R.string.dev_support)
                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    support?.isEnabled = false
                    support?.summary = "Loading..."
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                }

                override fun onAdOpened() {
                    support?.isEnabled = false
                    support?.summary = "Loading..."
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                }

                override fun onAdClicked() {
                    support?.isEnabled = false
                    support?.summary = "Loading..."
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                }

                override fun onAdLeftApplication() {
                    support?.isEnabled = false
                    support?.summary = "Loading..."
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                }

                override fun onAdClosed() {
                    support?.isEnabled = false
                    support?.summary = "Loading..."
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                }
            }
        }

        private fun goToPlayStore(context: Context?){
            val uri: Uri = Uri.parse("market://details?id=" + context?.packageName)
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                goToMarket.addFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY or
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                )
            }
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

        private fun isAvailable(list: ArrayList<String>,keyword:String):Int{
            var position = 0
            for (i in list){
                if (keyword==i){
                    return position
                }
                position += 1
            }

            return -1
        }

    }

    private fun isAvailable(list: ArrayList<String>,keyword:String):Int{
        var position = 0
        for (i in list){
            if (keyword==i){
                return position
            }
            position += 1
        }

        return position
    }

    override fun onBackPressed() {
        super.onBackPressed()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}