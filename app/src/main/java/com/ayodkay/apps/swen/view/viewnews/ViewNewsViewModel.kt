package com.ayodkay.apps.swen.view.viewnews

import android.app.Application
import android.net.Uri
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.BaseViewModel
import com.ayodkay.apps.swen.helper.CardClick
import com.ayodkay.apps.swen.helper.SimpleEvent
import com.ayodkay.apps.swen.helper.extentions.ifNull
import com.ayodkay.apps.swen.helper.trigger
import com.github.ayodkay.models.Article
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.google.mlkit.nl.languageid.LanguageIdentification
import org.json.JSONObject

private const val DOMAIN_URI_PREFIX = "https://swenio.page.link"

class ViewNewsViewModel(application: Application) : BaseViewModel(application), CardClick {
    var dynamicLink = ""
    val moreNews = ObservableArrayList<Article>()
    val listener = this

    private val languageIdentifier = LanguageIdentification.getClient()
    val languageCode = ObservableField("")
    val isPlaying = ObservableField(false)
    val isTalkingDrawable = ObservableField(R.drawable.ic_baseline_play_arrow_24)
    val showLoading = ObservableField(true)
    val showBottomSheet = ObservableField(false)
    val isCollapsed = ObservableField(true)

    val playEvent = SimpleEvent()
    val fullArticleEvent = SimpleEvent()
    val shareEvent = SimpleEvent()
    val viewImageEvent = SimpleEvent()

    fun stopLoading() {
        showLoading.set(false)
    }

    fun shareNews() {
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse(url)
            domainUriPrefix = DOMAIN_URI_PREFIX
            androidParameters {}
        }.addOnSuccessListener { result ->
            dynamicLink = result.shortLink.toString()
            shareEvent.trigger()
        }.addOnFailureListener {}
    }

    fun gotoWebView() {
        fullArticleEvent.trigger()
    }

    fun gotoViewImage() {
        viewImageEvent.trigger()
    }

    fun setUpLanguageIdentify() {
        languageIdentifier.identifyLanguage("$title. ${content.ifNull { description }}.")
            .addOnSuccessListener { code ->
                languageCode.set(code)
            }.addOnFailureListener {}
    }

    fun play() {
        playEvent.trigger()
    }

    override fun onCardClick(article: Article) {
        val props = JSONObject().put("source", "View News Fragment")
        mixpanel.track("Card Click", props)
        goToViewNewsFragment.trigger(article)
    }
}
