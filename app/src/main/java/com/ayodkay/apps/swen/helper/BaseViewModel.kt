package com.ayodkay.apps.swen.helper

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.applovin.mediation.MaxAd
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.event.SingleLiveEvent
import com.ayodkay.apps.swen.helper.mixpanel.MixPanelInterface
import com.ayodkay.apps.swen.helper.room.bookmarks.BookmarkRoomVM
import com.ayodkay.apps.swen.helper.room.links.Links
import com.github.ayodkay.models.Article
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    val nativeAdLoader = MaxNativeAdLoader("08f93b640def0007", application.applicationContext)
    val mixpanel: MixPanelInterface by inject()
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

    val getSelectedCountryDao = Helper.getCountryDatabase(application.applicationContext)
    val getSelectedLocationDao =
        Helper.getLocationDatabase(application.applicationContext).locationDao()
    val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(application.mainExecutor) { }
    }
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
