package com.ayodkay.apps.swen.helper

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.applovin.mediation.MaxAd
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.ayodkay.apps.swen.helper.event.SingleLiveEvent
import com.ayodkay.apps.swen.helper.room.bookmarks.BookmarkRoomVM
import com.ayodkay.apps.swen.helper.room.links.Links
import com.github.ayodkay.models.Article

open class BaseViewModel : ViewModel() {
    lateinit var nativeAdLoader: MaxNativeAdLoader
    var nativeAd: MaxAd? = null

    var source = ""
    var url = ""
    var image = ""
    var title = ""
    var content = ""
    var description = ""

    val loadAd = ObservableField(false)
    val bookMarkRoom = ObservableField<BookmarkRoomVM>()
    val goToViewNewsFragment = Event<Article>()
    val goToWebView = Event<String>()
}

interface CardClick {
    fun onCardClick(article: Article)
}

interface LinkCardClick {
    fun onCardClick(link: Links)
}

typealias Event<T> = SingleLiveEvent<T>
typealias SimpleEvent = Event<Unit>

fun <T> Event<T>.trigger(value: T) = postValue(value)
fun SimpleEvent.trigger() = postValue(Unit)