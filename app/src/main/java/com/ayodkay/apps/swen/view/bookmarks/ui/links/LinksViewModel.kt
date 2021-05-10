package com.ayodkay.apps.swen.view.bookmarks.ui.links

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayodkay.apps.swen.helper.App.Companion.context
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.room.links.Links

class LinksViewModel : ViewModel() {
    private val getLinks = Helper.getLinksDatabase(context).linksDao().getAll()
    private val _links = MutableLiveData<List<Links>>().apply {
        value = getLinks
    }
    val links: LiveData<List<Links>> = _links
}