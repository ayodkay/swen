package com.ayodkay.apps.swen.view.viewnews

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applovin.mediation.MaxAd
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.ayodkay.apps.swen.helper.CardClick
import com.ayodkay.apps.swen.helper.LinkCardClick
import com.ayodkay.apps.swen.helper.adapter.MaxAdsRecyclerView
import com.ayodkay.apps.swen.helper.room.bookmarks.BookmarkRoomVM
import com.ayodkay.apps.swen.helper.room.links.Links
import com.github.ayodkay.models.Article

@BindingAdapter(value = ["newsList", "bookmarkRoom", "nativeAdLoader", "nativeAd", "listener"])
fun RecyclerView.setNewsList(
    newsList: ArrayList<Article>?,
    bookmarkRoom: BookmarkRoomVM,
    nativeAdLoader: MaxNativeAdLoader,
    nativeAd: MaxAd? = null,
    listener: CardClick?
) {
    if (newsList != null) {
        if (adapter == null) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = MaxAdsRecyclerView(
                newsList,
                arrayListOf(),
                bookmarkRoom,
                nativeAdLoader,
                nativeAd,
                listener
            )
        } else {
            (adapter as MaxAdsRecyclerView).apply {
                this.newsList = newsList
                notifyDataSetChanged()
            }
        }
    }
}

@BindingAdapter("links", "linkNativeAdLoader", "linkNativeAd", "linkCardClick")
fun RecyclerView.setLinkList(
    links: ArrayList<Links>?,
    nativeAdLoader: MaxNativeAdLoader,
    nativeAd: MaxAd? = null,
    linkCardClick: LinkCardClick?
) {
    if (links != null) {
        if (adapter == null) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = MaxAdsRecyclerView(
                arrayListOf(),
                links = links,
                nativeAdLoader = nativeAdLoader,
                nativeAd = nativeAd,
                linkCardClick = linkCardClick,
                bookmarkRoomVM = null
            )
        } else {
            (adapter as MaxAdsRecyclerView).apply {
                this.links = links
                notifyDataSetChanged()
            }
        }
    }
}
