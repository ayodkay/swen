package com.ayodkay.apps.swen.helper.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.NativeCustomAdFrameBinding
import com.ayodkay.apps.swen.databinding.NewsLinksSavedBinding
import com.ayodkay.apps.swen.databinding.NewsListCardBinding
import com.ayodkay.apps.swen.helper.CardClick
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.LinkCardClick
import com.ayodkay.apps.swen.helper.extentions.ImageViewCallBack
import com.ayodkay.apps.swen.helper.extentions.ifNull
import com.ayodkay.apps.swen.helper.mixpanel.MixPanelInterface
import com.ayodkay.apps.swen.helper.room.bookmarks.BookMarkRoom
import com.ayodkay.apps.swen.helper.room.bookmarks.BookmarkRoomVM
import com.ayodkay.apps.swen.helper.room.links.Links
import com.github.ayodkay.models.Article
import java.text.SimpleDateFormat
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val ITEM_TYPE_COUNTRY by lazy { 0 }
private val ITEM_TYPE_MAX_AD by lazy { 1 }

class MaxAdsRecyclerView internal constructor(
    var newsList: ArrayList<Article>,
    var links: ArrayList<Links>,
    private var bookmarkRoomVM: BookmarkRoomVM? = null,
    private var nativeAdLoader: MaxNativeAdLoader,
    var nativeAd: MaxAd? = null,
    val listener: CardClick? = null,
    val linkCardClick: LinkCardClick? = null,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val isLinkView = links.isNotEmpty()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_MAX_AD -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = NativeCustomAdFrameBinding.inflate(layoutInflater, parent, false)
                MyAdViewHolder(binding, nativeAd)
            }

            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                if (isLinkView) {
                    val binding = NewsLinksSavedBinding.inflate(layoutInflater, parent, false)
                    LinkViewHolder(binding)
                } else {
                    val binding = NewsListCardBinding.inflate(layoutInflater, parent, false)
                    NewsViewHolder(binding)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isLinkView) links.size else newsList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyAdViewHolder -> {
                holder.bind(nativeAdLoader)
            }

            is NewsViewHolder -> {
                bookmarkRoomVM?.let {
                    holder.bind(
                        newsList.getOrNull(position) ?: return,
                        it, listener
                    )
                }
            }

            is LinkViewHolder -> {
                holder.bind(links.getOrNull(position) ?: return, linkCardClick)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if ((position + 1) % 4 == 0 && (position + 1) != 1) {
            return ITEM_TYPE_MAX_AD
        }
        return ITEM_TYPE_COUNTRY
    }

    // Banner Ad View Holder
    class MyAdViewHolder(val binding: NativeCustomAdFrameBinding, var nativeAd: MaxAd?) :
        RecyclerView.ViewHolder(binding.root), KoinComponent {
        private val mixpanel: MixPanelInterface by inject()
        fun bind(nativeAdLoader: MaxNativeAdLoader) {
            binding.loading = true
            binding.showError = false
            val binder: MaxNativeAdViewBinder =
                MaxNativeAdViewBinder.Builder(R.layout.native_custom_ad_view)
                    .setTitleTextViewId(R.id.title_text_view)
                    .setBodyTextViewId(R.id.body_text_view)
                    .setAdvertiserTextViewId(R.id.advertiser_textView)
                    .setIconImageViewId(R.id.icon_image_view)
                    .setMediaContentViewGroupId(R.id.media_view_container)
                    .setOptionsContentViewGroupId(R.id.options_view)
                    .setCallToActionButtonId(R.id.cta_button)
                    .build()
            val nativeAdView = MaxNativeAdView(binder, binding.root.context)
            nativeAdLoader.loadAd(nativeAdView)
            nativeAdLoader.setNativeAdListener(object :
                    MaxNativeAdListener() {
                    override fun onNativeAdLoaded(
                        nativeAdView: MaxNativeAdView?,
                        ad: MaxAd,
                    ) {
                        binding.loading = false
                        // Cleanup any pre-existing native ad to prevent memory leaks.
                        if (nativeAd != null) {
                            nativeAdLoader.destroy(nativeAd)
                        }

                        // Save ad for cleanup.
                        nativeAd = ad
                        // Add ad view to view.
                        binding.nativeAdLayout.removeAllViews()
                        binding.nativeAdLayout.addView(nativeAdView)
                        val props = JSONObject().put("source", "Native Ads")
                        mixpanel.track("Show Ads", props)
                    }

                    override fun onNativeAdLoadFailed(
                        adUnitId: String,
                        maxError: MaxError,
                    ) {
                        binding.loading = false
                        binding.showError = true
                    }

                    override fun onNativeAdClicked(ad: MaxAd) {}
                })
        }
    }

    class NewsViewHolder(val binding: NewsListCardBinding) :
        RecyclerView.ViewHolder(binding.root), ImageViewCallBack, KoinComponent {
        private val mixpanel: MixPanelInterface by inject()

        @SuppressLint("SimpleDateFormat")
        fun bind(newsPosition: Article, bookMarkRoom: BookmarkRoomVM, listener: CardClick?) {
            binding.loading = true
            binding.loadingCallback = this
            val url = newsPosition.url.ifNull { "" }
            val title = newsPosition.title.ifNull { "" }
            val author = newsPosition.author.ifNull { "" }
            val source = newsPosition.source.name.ifNull { "" }
            val urlToImage = newsPosition.urlToImage.ifNull { "" }
            val description =
                newsPosition.description.ifNull { "" }.replace(regex = Regex("<.*?>"), "")
            val content = newsPosition.content.ifNull { "" }.replace(regex = Regex("<.*?>"), "")
            val date = newsPosition.publishedAt
                .replace("T", " ").replace("Z", "")

            binding.image = urlToImage
            binding.source = source
            binding.date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date)?.toString()
            binding.title = title

            if (bookMarkRoom.exist(url)) {
                binding.bookmarkID = R.drawable.ic_round_bookmark_white
            } else {
                binding.bookmarkID = R.drawable.ic_round_bookmark_border_white
            }

            binding.bookmarkView.setOnClickListener {
                if (bookMarkRoom.exist(url)) {
                    bookMarkRoom.deleteOne(url)
                    binding.bookmarkID = R.drawable.ic_round_bookmark_border_white
                } else {
                    newsBookMark()
                    bookMarkRoom.insert(
                        BookMarkRoom(
                            url = url, source = source, author = author, title = title,
                            description = description, urlToImage = urlToImage, publishedAt = date,
                            content = content,
                        )
                    )
                    binding.bookmarkID = R.drawable.ic_round_bookmark_white
                }
            }
            binding.root.setOnClickListener {
                listener?.onCardClick(newsPosition)
            }
            binding.executePendingBindings()
        }

        private fun newsBookMark() {
            val props = JSONObject().put("source", "Category Fragment")
            mixpanel.track("News BookMark", props)
        }

        override fun onLoadingDone() {
            binding.loading = false
        }
    }

    class LinkViewHolder(val binding: NewsLinksSavedBinding) :
        RecyclerView.ViewHolder(binding.root), KoinComponent {
        private val mixpanel: MixPanelInterface by inject()

        @SuppressLint("SimpleDateFormat")
        fun bind(link: Links, listener: LinkCardClick?) {
            binding.link = link.link.ifNull { "" }

            if (Helper.getLinksDatabase(binding.root.context).linksDao().exist(link.link)) {
                binding.drawableId = R.drawable.ic_bookmarked
            } else {
                binding.drawableId = R.drawable.ic_bookmark
            }

            binding.bookmarkView.setOnClickListener {
                if (Helper.getLinksDatabase(binding.root.context).linksDao().exist(link.link)) {
                    Helper.getLinksDatabase(binding.root.context).linksDao().deleteOne(link.link)
                    binding.drawableId = R.drawable.ic_bookmark
                } else {
                    newsBookMark()
                    Helper.getLinksDatabase(binding.root.context).linksDao()
                        .insertAll(Links(link = link.link))
                    binding.drawableId = R.drawable.ic_bookmarked
                }
            }
            binding.root.setOnClickListener {
                listener?.onCardClick(link)
            }
            binding.executePendingBindings()
        }

        private fun newsBookMark() {
            val props = JSONObject().put("source", "Link Fragment")
            mixpanel.track("Link Bookmark", props)
        }
    }
}
