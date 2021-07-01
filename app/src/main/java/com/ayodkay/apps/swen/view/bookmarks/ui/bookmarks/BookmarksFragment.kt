package com.ayodkay.apps.swen.view.bookmarks.ui.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.adapter.RoomRecyclerview
import com.ayodkay.apps.swen.helper.room.bookmarks.BookmarkRoomVM
import com.ayodkay.apps.swen.model.News
import com.mopub.nativeads.MoPubRecyclerAdapter
import com.mopub.nativeads.MoPubStaticNativeAdRenderer
import com.mopub.nativeads.RequestParameters
import com.mopub.nativeads.ViewBinder
import java.util.*

class BookmarksFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_bookmarks, container, false)
        val noSaved = root.findViewById<ImageView>(R.id.no_saved)
        val savedRecycle = root.findViewById<RecyclerView>(R.id.saved_recycle)

        val news: ArrayList<News> = arrayListOf()

        val bookmarkModel = ViewModelProvider(this).get(BookmarkRoomVM::class.java)
        var add = true
        bookmarkModel.allBookMarkRoom.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                noSaved.visibility = View.VISIBLE
                savedRecycle.visibility = View.GONE
            } else {
                for (i in it.indices) {
                    if (add) {
                        news.add(
                            News(
                                it[i].source,
                                it[i].author,
                                it[i].title,
                                it[i].description,
                                it[i].url,
                                it[i].urlToImage,
                                it[i].publishedAt,
                                it[i].content,
                            )
                        )

                    }
                }
                add = false
            }

            val desiredAssets = EnumSet.of(
                RequestParameters.NativeAdAsset.TITLE,
                RequestParameters.NativeAdAsset.TEXT,
                RequestParameters.NativeAdAsset.ICON_IMAGE,
                RequestParameters.NativeAdAsset.MAIN_IMAGE,
                RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT,
                RequestParameters.NativeAdAsset.SPONSORED
            )
            val requestParameters = RequestParameters.Builder()
                .desiredAssets(desiredAssets)
                .build()
            val moPubStaticNativeAdRenderer = MoPubStaticNativeAdRenderer(
                ViewBinder.Builder(R.layout.native_ad_list_item)
                    .titleId(R.id.native_title)
                    .textId(R.id.native_text)
                    .mainImageId(R.id.native_main_image)
                    .iconImageId(R.id.native_icon_image)
                    .callToActionId(R.id.native_cta)
                    .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                    .sponsoredTextId(R.id.native_sponsored_text_view)
                    .build()
            )

            MoPubRecyclerAdapter(
                requireActivity(), RoomRecyclerview(news, this@BookmarksFragment, requireContext())
            ).apply {
                registerAdRenderer(moPubStaticNativeAdRenderer)
            }.also {
                savedRecycle.apply {
                    adapter = it
                    layoutManager = LinearLayoutManager(requireContext())
                    it.loadAds(getString(R.string.mopub_adunit_native), requestParameters)
                }
            }
        })

        return root
    }
}