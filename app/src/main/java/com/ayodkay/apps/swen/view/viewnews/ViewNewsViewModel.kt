package com.ayodkay.apps.swen.view.viewnews

import android.net.Uri
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.ayodkay.apps.swen.helper.BaseViewModel
import com.ayodkay.apps.swen.helper.CardClick
import com.ayodkay.apps.swen.helper.SimpleEvent
import com.ayodkay.apps.swen.helper.trigger
import com.github.ayodkay.models.Article
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.google.mlkit.nl.languageid.LanguageIdentification

private const val DOMAIN_URI_PREFIX = "https://swenio.page.link"

class ViewNewsViewModel : BaseViewModel(), CardClick {
    var dynamicLink = ""
    val moreNews = ObservableArrayList<Article>()
    val listener = this

    private val languageIdentifier = LanguageIdentification.getClient()
    val languageCode = ObservableField("")
    val showPlayButton = ObservableField(false)
    val showStopButton = ObservableField(false)
    val showLoading = ObservableField(true)
    val showBottomSheet = ObservableField(false)
    val bottomSheetState = ObservableField(0)


    val playEvent = SimpleEvent()
    val stopEvent = SimpleEvent()
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
        languageIdentifier.identifyLanguage(title)
            .addOnSuccessListener { code ->
                languageCode.set(code)
            }.addOnFailureListener {}
    }

    fun play() {
        playEvent.trigger()
    }

    fun stop() {
        stopEvent.trigger()
    }

    override fun onCardClick(article: Article) {
        goToViewNewsFragment.trigger(article)
    }
}