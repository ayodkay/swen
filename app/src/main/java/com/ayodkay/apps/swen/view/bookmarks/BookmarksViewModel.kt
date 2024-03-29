package com.ayodkay.apps.swen.view.bookmarks

import android.app.Application
import androidx.databinding.ObservableArrayList
import com.ayodkay.apps.swen.view.BaseViewModel
import com.ayodkay.apps.swen.view.CardClick
import com.ayodkay.apps.swen.view.trigger
import com.github.ayodkay.models.Article
import org.json.JSONObject

class BookmarksViewModel(application: Application) : BaseViewModel(application), CardClick {
    val news = ObservableArrayList<Article>()
    val listener = this
    override fun onCardClick(article: Article) {
        val props = JSONObject().put("source", "Bookmark Fragment")
        mixpanel.track("Card Click", props)
        goToViewNewsFragment.trigger(article)
    }
}
