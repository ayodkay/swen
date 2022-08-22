package com.ayodkay.apps.swen.view.search

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.ayodkay.apps.swen.view.BaseViewModel
import com.ayodkay.apps.swen.view.CardClick
import com.ayodkay.apps.swen.view.Event
import com.ayodkay.apps.swen.view.SimpleEvent
import com.ayodkay.apps.swen.view.trigger
import com.github.ayodkay.models.Article
import org.json.JSONObject

class SearchViewModel(application: Application) : BaseViewModel(application), CardClick {
    var checkedSort = 1
    lateinit var sort: String
    var sortOptions = arrayListOf("popularity", "publishedAt", "relevancy")

    val showBannerAd = ObservableField(true)
    val showEmpty = ObservableField(false)
    val query = ObservableField("")

    val newsList = ObservableArrayList<Article>()
    val listener = this

    val emptyTextValue = ObservableField("")
    val sortEvent = SimpleEvent()
    val searchEvent = Event<String>()

    fun sort() {
        sortEvent.trigger()
    }

    val doOnQueryTextListener = object : SearchViewListener {
        override fun onQueryTextSubmit(s: String) {
            query.set(s)
            val props = JSONObject().put("search", s)
            mixpanel.track("Search News", props)
            searchEvent.trigger(s)
        }
    }

    override fun onCardClick(article: Article) {
        val props = JSONObject().put("source", "Search Fragment")
        mixpanel.track("Card Click", props)
        goToViewNewsFragment.trigger(article)
    }
}
