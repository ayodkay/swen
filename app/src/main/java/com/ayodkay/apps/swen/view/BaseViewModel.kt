package com.ayodkay.apps.swen.view

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.applovin.mediation.MaxAd
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.event.SingleLiveEvent
import com.ayodkay.apps.swen.helper.firebase.config.ConfigInterface
import com.ayodkay.apps.swen.helper.mixpanel.MixPanelInterface
import com.ayodkay.apps.swen.helper.room.bookmarks.BookmarkRoomVM
import com.ayodkay.apps.swen.helper.room.links.Links
import com.github.ayodkay.models.Article
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    val nativeAdLoader = MaxNativeAdLoader("08f93b640def0007", application.applicationContext)
    val mixpanel: MixPanelInterface by inject()
    val remoteConfig: ConfigInterface by inject()
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

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(
                ContextCompat.getMainExecutor(application.applicationContext)
            ) { }
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
