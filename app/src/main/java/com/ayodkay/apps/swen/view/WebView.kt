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
import androidx.core.content.res.ResourcesCompat
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.AppLog
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.room.links.Links
import com.ayodkay.apps.swen.view.main.MainActivity
import com.ayodkay.apps.swen.view.webview.WebViewSuite
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

        val url  = intent.extras?.get("url") as String

        webViewSuite.startLoading(url)

        back_button.setOnClickListener {
            onBackPressed()
        }

        refresh.setOnClickListener {
            webViewSuite.refresh()
        }

        shareLink.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, url)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        webViewSuite.customizeClient(object : WebViewSuite.WebViewSuiteCallback {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {}

            override fun onPageFinished(view: WebView?, url: String?) {
                val slash = url!!.indexOf("//") + 2
                val domain = url.substring(slash, url.indexOf('/', slash))
                urlLink.text = domain
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return true
            }
        })

        saveLink.setOnClickListener {
            Helper.getLinksDatabase(this).linksDao().insertAll(Links(link = url))
        }

        if (Helper.getLinksDatabase(this).linksDao()
                .exist(url)){
            saved.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_bookmarked, null))
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