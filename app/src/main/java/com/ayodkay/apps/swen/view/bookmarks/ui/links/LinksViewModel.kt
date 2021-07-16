package com.ayodkay.apps.swen.view.bookmarks.ui.links

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.room.links.Links

class LinksViewModel internal constructor(application: Application) :
    AndroidViewModel(application) {
    private val getLinks = Helper.getLinksDatabase(getApplication()).linksDao().getAll()
    private val _links = MutableLiveData<List<Links>>().apply {
        value = getLinks
    }
    val links: LiveData<List<Links>> = _links
}