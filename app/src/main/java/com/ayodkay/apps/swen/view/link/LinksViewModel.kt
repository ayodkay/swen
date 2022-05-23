package com.ayodkay.apps.swen.view.link

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ayodkay.apps.swen.helper.BaseViewModel
import com.ayodkay.apps.swen.helper.LinkCardClick
import com.ayodkay.apps.swen.helper.room.links.Links
import com.ayodkay.apps.swen.helper.trigger

class LinksViewModel(application: Application) : BaseViewModel(application), LinkCardClick {
    val links = ObservableArrayList<Links>()
    val mutableLink = MutableLiveData<List<Links>>()
    val observableLink: LiveData<List<Links>> = mutableLink
    val emptyLink = ObservableField(false)
    val listener = this

    override fun onCardClick(link: Links) {
        goToWebView.trigger(link.link)
    }
}
