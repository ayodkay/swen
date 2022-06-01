package com.ayodkay.apps.swen.view.settings

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.applovin.mediation.ads.MaxAdView
import com.ayodkay.apps.swen.R

class AdsPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {
    private lateinit var adsPreference: MaxAdView

    init {
        widgetLayoutResource = R.layout.settings_ads
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        adsPreference = holder.itemView.findViewById(R.id.settingsAds) as MaxAdView
        adsPreference.apply {
            loadAd()
            startAutoRefresh()
        }
    }
}
