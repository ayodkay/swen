package com.ayodkay.apps.swen.view.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.AppLog
import com.ayodkay.apps.swen.view.AskLocation
import com.ayodkay.apps.swen.view.ThemeActivity
import com.ayodkay.apps.swen.view.main.MainActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.settings_activity.*


class SettingsActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {

            }
            Configuration.UI_MODE_NIGHT_YES -> {
                setTheme(R.style.AppThemeNight)
                background.setBackgroundColor(resources.getColor(R.color.colorPrimary))
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

            val feedback: EditTextPreference? = findPreference("feedback")
            val country: Preference? = findPreference("country")
            val share: Preference? = findPreference("share")
            val rate: Preference? = findPreference("rate")
            val color: Preference? = findPreference("color")
            val support: Preference? = findPreference("support")


            setupAds(support)


            feedback?.setOnPreferenceChangeListener { _, newValue ->
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "message/rfc822"
                intent.putExtra(Intent.EXTRA_TEXT, newValue.toString())
                intent.putExtra(Intent.EXTRA_SUBJECT, "feedback and suggestions")
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("ayodelekayode51@yahoo.com"))
                val mailer = Intent.createChooser(intent, null)
                startActivity(mailer)
                true
            }


            country?.setOnPreferenceClickListener {

                startActivity(Intent(this.context,AskLocation::class.java))

                true
            }

            share?.setOnPreferenceClickListener {

                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "${resources.getString(R.string.share_app)}\n${resources.getString(R.string.google_play)}")
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)

                true
            }

            rate?.setOnPreferenceClickListener {

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
                    startActivity(goToMarket)
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + context?.packageName)
                        )
                    )
                }

                true
            }

            color?.setOnPreferenceClickListener {

                startActivity(Intent(this.context,ThemeActivity::class.java))

                true
            }

            support?.setOnPreferenceClickListener {
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                } else {
                   AppLog.Log("TAG", "The interstitial wasn't loaded yet.")
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
                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    support?.isEnabled = false
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                }

                override fun onAdOpened() {
                    support?.isEnabled = false
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                }

                override fun onAdClicked() {
                    support?.isEnabled = false
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                }

                override fun onAdLeftApplication() {
                    support?.isEnabled = false
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                }

                override fun onAdClosed() {
                    support?.isEnabled = false
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                }
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()

        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}