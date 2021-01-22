package com.ayodkay.apps.swen.view

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.ClientCertRequest
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.AppLog
import com.ayodkay.apps.swen.view.main.MainActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.activity_web_view.*

class WebView : AppCompatActivity() {

    private lateinit var mInterstitialAd: InterstitialAd
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        mInterstitialAd = InterstitialAd(this)

        mInterstitialAd.adUnitId = "ca-app-pub-7312232171503509/8595637711"

        mInterstitialAd.loadAd(AdRequest.Builder().build())

        back_button.setOnClickListener {
            onBackPressed()
        }

        webview.apply {
            loadUrl(intent.extras?.get("url") as String)
            settings.javaScriptEnabled = true
            settings.defaultTextEncodingName = "utf-8"
        }

        webview.webViewClient = object : WebViewClient(){
            override fun onReceivedClientCertRequest(view: WebView?, request: ClientCertRequest?) {
                super.onReceivedClientCertRequest(view, request)
                AppLog.log(message = request.toString())
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                val slash = url!!.indexOf("//") + 2
                val domain = url.substring(slash, url.indexOf('/', slash))
                urlLink.text = domain
                webProgress.visibility = VISIBLE

            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val slash = url!!.indexOf("//") + 2
                val domain = url.substring(slash, url.indexOf('/', slash))
                urlLink.text = domain
                webProgress.visibility = GONE
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return true
            }

        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        mInterstitialAd.show()
        val toMain = intent.extras?.get("toMain") as Boolean
        if (toMain){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}