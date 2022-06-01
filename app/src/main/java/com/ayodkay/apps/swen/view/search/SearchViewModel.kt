package com.ayodkay.apps.swen.view.search

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.ayodkay.apps.swen.helper.*
import com.github.ayodkay.models.Article

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
            searchEvent.trigger(s)
        }
    }

    override fun onCardClick(article: Article) {
        goToViewNewsFragment.trigger(article)
    }
}
