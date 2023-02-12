package com.ayodkay.apps.swen.view.home.category

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.ayodkay.apps.swen.view.BaseViewModel
import com.ayodkay.apps.swen.view.CardClick
import com.ayodkay.apps.swen.view.trigger
import com.github.ayodkay.models.Article
import org.json.JSONObject

class CategoryViewModel(application: Application) : BaseViewModel(application), CardClick {
    var category = ""
    var q = ""
    var country = ""
    var language = ""

    val loading = ObservableBoolean(true)
    val refreshing = ObservableBoolean(false)
    val emptyNews = ObservableBoolean(false)
    val emptyText = ObservableField("Not Found")

    val newsResponseList = ObservableArrayList<Article>()
    val listener = this

    override fun onCardClick(article: Article) {
        val props = JSONObject().put("source", category)
        mixpanel.track("Card Click", props)
        goToViewNewsFragment.trigger(article)
    }
}
