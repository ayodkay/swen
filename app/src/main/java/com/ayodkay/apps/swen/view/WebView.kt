package com.ayodkay.apps.swen.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ayodkay.apps.swen.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_web_view.*

class WebView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        webview.loadUrl(intent.extras?.get("url") as String)

    }
}