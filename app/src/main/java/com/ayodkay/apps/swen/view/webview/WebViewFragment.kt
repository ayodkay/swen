package com.ayodkay.apps.swen.view.webview

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.FragmentWebviewBinding
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.room.links.Links
import com.ayodkay.apps.swen.view.BaseFragment
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.math.pow
import org.json.JSONObject

class WebViewFragment : BaseFragment(), MaxAdListener {
    private lateinit var binding: FragmentWebviewBinding
    private val webViewViewModel: WebViewViewModel by viewModels()
    private val args: WebViewFragmentArgs by navArgs()

    private lateinit var interstitialAd: MaxInterstitialAd
    private var retryAttempt = 0.0

    private fun createInterstitialAd() {
        interstitialAd = MaxInterstitialAd("27b235607499900d", requireActivity())
        interstitialAd.setListener(this)

        // Load the first ad
        interstitialAd.loadAd()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentWebviewBinding.inflate(inflater, container, false).apply {
        createInterstitialAd()
        viewModel = webViewViewModel
        webViewViewModel.webLink.set(args.link)
        webViewViewModel.navigateToMain = args.navigateToMain
        webViewViewModel.bookmarkDrawable.set(
            if (Helper.getLinksDatabase(requireContext()).linksDao().exist(args.link)) {
                R.drawable.ic_bookmarked
            } else R.drawable.ic_bookmark
        )
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed { changeView() }

        val linkDao = Helper.getLinksDatabase(requireContext()).linksDao()

        webViewViewModel.refreshPage.observe(viewLifecycleOwner) {
            webViewViewModel.mixpanel.track("Refresh Page")
            binding.webViewSuite.refresh()
        }

        webViewViewModel.gotBack.observe(viewLifecycleOwner) {
            webViewViewModel.mixpanel.track("Go Back")
            changeView()
        }

        webViewViewModel.shareUrl.observe(viewLifecycleOwner) {
            val props = JSONObject()
            props.put("source", "WebView Fragment")
            props.put("url", webViewViewModel.webLink.get().orEmpty())
            webViewViewModel.mixpanel.track("Share News", props)
            share(webViewViewModel.webLink.get().orEmpty())
        }

        webViewViewModel.openBrowser.observe(viewLifecycleOwner) {
            val props = JSONObject().put("link", webViewViewModel.webLink.get().orEmpty())
            webViewViewModel.mixpanel.track("Open In Browser", props)
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(webViewViewModel.webLink.get().orEmpty()))
            startActivity(browserIntent)
        }

        webViewViewModel.bookmarkUrl.observe(viewLifecycleOwner) {
            if (linkDao.exist(webViewViewModel.webLink.get().orEmpty())) {
                linkDao.deleteOne(webViewViewModel.webLink.get().orEmpty())
                webViewViewModel.bookmarkDrawable.set(R.drawable.ic_bookmark)
            } else {
                val props = JSONObject().put("source", "WebView Fragment")
                webViewViewModel.mixpanel.track("Link Bookmark", props)
                Helper.getLinksDatabase(requireContext()).linksDao()
                    .insertAll(Links(link = webViewViewModel.webLink.get().orEmpty()))
                webViewViewModel.bookmarkDrawable.set(R.drawable.ic_bookmarked)
            }
        }

        binding.webViewSuite.customizeClient(object :
                WebViewSuite.WebViewSuiteCallback {
                override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
                    val slash = url.indexOf("//") + 2
                    val domain = url.substring(slash, url.indexOf('/', slash))
                    webViewViewModel.webDomain.set(domain)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    val slash = url!!.indexOf("//") + 2
                    val domain = url.substring(slash, url.indexOf('/', slash))
                    webViewViewModel.webDomain.set(domain)
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.loadUrl(url!!)
                    return true
                }
            })
    }

    private fun share(url: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun changeView() {
        if (interstitialAd.isReady) {
            interstitialAd.showAd()
        }
        navigate()
    }

    override fun onAdLoaded(ad: MaxAd?) {
        retryAttempt = 0.0
    }

    override fun onAdHidden(ad: MaxAd?) {
        interstitialAd.loadAd()
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        retryAttempt++
        val delayMillis = TimeUnit.SECONDS.toMillis(2.0.pow(min(6.0, retryAttempt)).toLong())
        Handler(Looper.getMainLooper()).postDelayed({ interstitialAd.loadAd() }, delayMillis)
    }

    override fun onAdDisplayed(ad: MaxAd?) {
        val props = JSONObject().put("source", "Interstitial Ad")
        webViewViewModel.mixpanel.track("Show Ads", props)
    }

    override fun onAdClicked(ad: MaxAd?) {}
    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {}

    private fun navigate() {
        if (webViewViewModel.navigateToMain) {
            navigateTo(WebViewFragmentDirections.actionNavWebViewToNavMainSwen())
        } else {
            navigateUp()
        }
    }
}
