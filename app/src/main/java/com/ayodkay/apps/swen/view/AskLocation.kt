package com.ayodkay.apps.swen.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.AppLog
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.countrypicker.CountryPicker
import com.ayodkay.apps.swen.helper.countrypicker.CountryPickerListener
import com.ayodkay.apps.swen.helper.room.country.Country
import com.ayodkay.apps.swen.view.main.MainActivity
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubView
import kotlinx.android.synthetic.main.activity_ask_location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AskLocation : AppCompatActivity(), MoPubView.BannerAdListener {
    private lateinit var moPubView: MoPubView

    override fun onStart() {
        super.onStart()

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {

            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                setTheme(R.style.AppThemeNight)
            } // Night mode is active, we're using dark theme
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        moPubView.destroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_location)

//        MobileAds.initialize(this)
//        val adRequest = AdRequest.Builder().build()
//        adView.loadAd(adRequest)
        moPubView = findViewById(R.id.banner_mopubview)
        moPubView.apply {
            bannerAdListener = this@AskLocation
            setAdUnitId("6f7de7b31dc2495e99f3643ce8ddda44")
            loadAd()
        }

        select_country.setOnClickListener {
            val picker =
                CountryPicker.getInstance(
                    resources.getString(R.string.select_country),
                    object : CountryPickerListener {
                        override fun onSelectCountry(
                            name: String,
                            code: String,
                            iso: String,
                            language: String
                        ) {
                            AppLog.l(iso)
                            GlobalScope.launch {
                                val db = Helper.getCountryDatabase(applicationContext)
                                db.countryDao().delete()
                                db.countryDao().insertAll(
                                    Country(
                                        iso,
                                        language
                                    )
                                )
                            }
                            val dialogFragment: DialogFragment? =
                                supportFragmentManager.findFragmentByTag("CountryPicker") as DialogFragment?
                            dialogFragment?.dismiss()

                            startActivity(
                                Intent(
                                    this@AskLocation,
                                    MainActivity::class.java
                                )
                            )

                            finish()
                        }
                    })

            picker.show(supportFragmentManager, "CountryPicker")
        }


    }

    override fun onBannerLoaded(p0: MoPubView) {
        AppLog.l("________onBannerLoaded________")
    }

    override fun onBannerFailed(p0: MoPubView?, p1: MoPubErrorCode?) {
        AppLog.l("________onBannerFailed________//${p1.toString()}")
    }

    override fun onBannerClicked(p0: MoPubView?) {
        AppLog.l("________onBannerClicked________")
    }

    override fun onBannerExpanded(p0: MoPubView?) {
        AppLog.l("________onBannerExpanded________")
    }

    override fun onBannerCollapsed(p0: MoPubView?) {
        AppLog.l("________onBannerCollapsed________")
    }
}