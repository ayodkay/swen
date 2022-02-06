package com.ayodkay.apps.swen.view

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.ayodkay.apps.swen.databinding.ActivityWebViewBinding
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.room.links.Links
import com.ayodkay.apps.swen.view.main.MainActivity
import com.ayodkay.apps.swen.view.webview.WebViewSuite
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.mopub.common.MoPub
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubInterstitial


class WebView : AppCompatActivity(), MoPubInterstitial.InterstitialAdListener {
    private lateinit var binding: ActivityWebViewBinding
    private var moPubInterstitial: MoPubInterstitial? = null

    private var mInterstitialAd: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadAdmob()
        val link = intent.extras?.get("url") as String

        binding.webViewSuite.startLoading(link)

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.refresh.setOnClickListener {
            binding.webViewSuite.refresh()
        }
        binding.openBrowser.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(browserIntent)
        }

        binding.webViewSuite.customizeClient(object : WebViewSuite.WebViewSuiteCallback {
            var reload = true
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                val slash = url!!.indexOf("//") + 2
                val domain = url.substring(slash, url.indexOf('/', slash))
                binding.urlLink.text = domain

                binding.shareLink.setOnClickListener {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, url)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {}

            override fun onPageCommitVisible(view: WebView?, url: String?) {
                if (link.contains("news.google.com")) {
                    if (reload) {
                        binding.webViewSuite.startLoading(url)
                        reload = false
                    }
                }

                val slash = url!!.indexOf("//") + 2
                val domain = url.substring(slash, url.indexOf('/', slash))
                binding.urlLink.text = domain

                binding.shareLink.setOnClickListener {
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

        binding.saveLink.setOnClickListener {
            Helper.getLinksDatabase(this).linksDao().insertAll(Links(link = link))

            binding.removeLink.visibility = VISIBLE
            binding.saveLink.visibility = GONE
        }

        binding.removeLink.setOnClickListener {
            Helper.getLinksDatabase(this).linksDao().deleteOne(link)

            binding.removeLink.visibility = GONE
            binding.saveLink.visibility = VISIBLE
        }

        binding.refresh.setOnClickListener {
            binding.webViewSuite.refresh()
        }

        if (Helper.getLinksDatabase(this).linksDao().exist(link)) {
            binding.removeLink.visibility = VISIBLE
            binding.saveLink.visibility = GONE
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            if (moPubInterstitial != null) {
                if (moPubInterstitial!!.isReady) {
                    moPubInterstitial!!.show()
                }
            }

        }
        changeView()
    }

    override fun onPause() {
        super.onPause()
        MoPub.onPause(this)
    }

    override fun onStop() {
        super.onStop()
        MoPub.onStop(this)
    }

    override fun onResume() {
        super.onResume()
        MoPub.onResume(this)
    }

    override fun onInterstitialLoaded(p0: MoPubInterstitial?) {}
    override fun onInterstitialFailed(p0: MoPubInterstitial?, p1: MoPubErrorCode?) {
        loadAdmob()
    }

    override fun onInterstitialShown(p0: MoPubInterstitial?) {}
    override fun onInterstitialClicked(p0: MoPubInterstitial?) {}
    override fun onInterstitialDismissed(p0: MoPubInterstitial?) {
        mInterstitialAd = null
    }


    private fun loadMopub() {
        moPubInterstitial = MoPubInterstitial(this, "7255cbc578d1408a913044bfc5759fa9")
        moPubInterstitial!!.interstitialAdListener = this
        moPubInterstitial!!.load()
    }

    private fun loadAdmob() {
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, "ca-app-pub-7312232171503509/8595637711",
            adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                    loadMopub()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })
    }

    private fun changeView() {
        val toMain = intent.extras?.get("toMain") as Boolean
        if (toMain) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}