package com.ayodkay.apps.swen.view.bookmarks.ui.bookmarks

import androidx.databinding.ObservableArrayList
import com.ayodkay.apps.swen.helper.BaseViewModel
import com.ayodkay.apps.swen.helper.CardClick
import com.ayodkay.apps.swen.helper.trigger
import com.github.ayodkay.models.Article

class BookmarksViewModel : BaseViewModel(), CardClick {
    val news = ObservableArrayList<Article>()
    val listener = this

    override fun onCardClick(article: Article) {
        goToViewNewsFragment.trigger(article)
    }
}