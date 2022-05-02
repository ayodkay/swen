package com.ayodkay.apps.swen.view.home.category

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.ayodkay.apps.swen.helper.BaseViewModel
import com.ayodkay.apps.swen.helper.CardClick
import com.ayodkay.apps.swen.helper.trigger
import com.github.ayodkay.models.Article

class CategoryViewModel : BaseViewModel(), CardClick {
    var category = ""
    var q = ""
    var country = ""
    var language = ""
    var isEveryThing = false


    val loading = ObservableBoolean(true)
    val refreshing = ObservableBoolean(false)
    val emptyNews = ObservableBoolean(false)
    val emptyText = ObservableField("Not Found")


    val newsResponseList = ObservableArrayList<Article>()
    val listener = this

    override fun onCardClick(article: Article) {
        goToViewNewsFragment.trigger(article)
    }
}