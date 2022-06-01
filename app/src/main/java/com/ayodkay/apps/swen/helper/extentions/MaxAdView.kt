package com.ayodkay.apps.swen.helper.extentions

import androidx.databinding.BindingAdapter
import com.applovin.mediation.ads.MaxAdView

@BindingAdapter("app:loadBannerAd")
fun MaxAdView.loadBannerAd(load: Boolean) {
    if (load) {
        this.loadAd()
        this.startAutoRefresh()
    }
}
