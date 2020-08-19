package com.ayodkay.apps.swen.view

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.View.*
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.net.toUri
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.AppLog
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


        val data: Uri = if (intent?.data != null){
            intent?.data!!
        }else{
            "".toUri()
        }

        val action: String = if (intent?.action != null){
            intent?.action!!
        }else{
            ""
        }


        AppLog.log("webView",action)
        AppLog.log("webView",data)

        webview.apply {
            loadUrl(intent.extras?.get("url") as String)
            settings.javaScriptEnabled = true
            settings.defaultTextEncodingName = "utf-8"
        }
        webview.webViewClient = object : WebViewClient(){

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                webProgress.visibility = VISIBLE

            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                webProgress.visibility = GONE
            }

        }

    }
}