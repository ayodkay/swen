package com.ayodkay.apps.swen.view

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.ayodkay.apps.swen.R
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

        val link = intent.extras?.get("url") as String

        webViewSuite.startLoading(link)

        back_button.setOnClickListener {
            onBackPressed()
        }

        refresh.setOnClickListener {
            webViewSuite.refresh()
        }

        webViewSuite.customizeClient(object : WebViewSuite.WebViewSuiteCallback {
            var reload = true
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                val slash = url!!.indexOf("//") + 2
                val domain = url.substring(slash, url.indexOf('/', slash))
                urlLink.text = domain

                shareLink.setOnClickListener {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, url)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (link.contains("news.google.com")) {
                    if (reload) {
                        webViewSuite.startLoading(url)
                        reload = false
                    }
                }

                val slash = url!!.indexOf("//") + 2
                val domain = url.substring(slash, url.indexOf('/', slash))
                urlLink.text = domain

                shareLink.setOnClickListener {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, url)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url.orEmpty().contains("youtube.com")) {
                    view?.loadUrl(url!!)
                }
                return true
            }
        })

        saveLink.setOnClickListener {
            Helper.getLinksDatabase(this).linksDao().insertAll(Links(link = link))

            removeLink.visibility = VISIBLE
            saveLink.visibility = GONE
        }

        removeLink.setOnClickListener {
            Helper.getLinksDatabase(this).linksDao().deleteOne(link)

            removeLink.visibility = GONE
            saveLink.visibility = VISIBLE
        }

        refresh.setOnClickListener {
            webViewSuite.refresh()
        }

        if (Helper.getLinksDatabase(this).linksDao().exist(link)) {
            removeLink.visibility = VISIBLE
            saveLink.visibility = GONE
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