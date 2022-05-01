package com.ayodkay.apps.swen.helper.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.ads.NativeTemplateStyle
import com.ayodkay.apps.swen.helper.ads.TemplateView
import com.ayodkay.apps.swen.helper.room.bookmarks.BookMarkRoom
import com.ayodkay.apps.swen.helper.room.bookmarks.BookmarkRoomVM
import com.ayodkay.apps.swen.view.viewnews.ViewNewActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.appevents.AppEventsLogger
import com.github.ayodkay.models.Article
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAdOptions
import java.text.SimpleDateFormat


private val ITEM_TYPE_COUNTRY by lazy { 0 }
private val ITEM_TYPE_BANNER_AD by lazy { 1 }

class AdMobRecyclerView internal constructor(
    private val newsList: ArrayList<Article>, private val owner: ViewModelStoreOwner,
    private val context: Context,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val activity = context as Activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ITEM_TYPE_BANNER_AD -> {
                val bannerLayoutView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.admob, parent, false)

                return MyAdViewHolder(bannerLayoutView)
            }

            else -> {
                val view: View =
                    LayoutInflater.from(context)
                        .inflate(R.layout.news_list_card_old, parent, false)
                return NewsViewHolder(view)
            }


        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    @SuppressLint("SimpleDateFormat")
    @Suppress("SENSELESS_COMPARISON")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE_BANNER_AD -> {
                val progressBar: LottieAnimationView =
                    holder.itemView.findViewById(R.id.adsProgress)

                val error: LottieAnimationView =
                    holder.itemView.findViewById(R.id.error)
                val template: TemplateView = holder.itemView.findViewById(R.id.my_template)
                MobileAds.initialize(context)
                val background =
                    ColorDrawable(ContextCompat.getColor(context, R.color.ads_background))

                AdLoader.Builder(context, context.resources.getString(R.string.custom_ads_unit))
                    .forNativeAd { nativeAd ->
                        val styles = NativeTemplateStyle.Builder()
                            .withMainBackgroundColor(background).build()
                        template.setStyles(styles)
                        template.setNativeAd(nativeAd)
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            error.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            template.visibility = View.GONE
                        }

                        override fun onAdLoaded() {
                            progressBar.visibility = View.GONE
                            error.visibility = View.GONE
                            template.visibility = View.VISIBLE

                        }
                    })
                    .withNativeAdOptions(NativeAdOptions.Builder()
                        .setRequestCustomMuteThisAd(true)
                        .build())
                    .build().apply {
                        activity.runOnUiThread {
                            loadAd(AdRequest.Builder().build())
                        }
                    }
            }

            else -> {
                val newsViewHolder: NewsViewHolder = holder as NewsViewHolder
                val newsModel = ViewModelProvider(owner).get(BookmarkRoomVM::class.java)
                val newsPosition = newsList[position]

                val author =
                    if (newsPosition.author != null) newsPosition.author else ""
                val title =
                    if (newsPosition.title != null) newsPosition.title else ""
                val description =
                    if (newsPosition.description != null) newsPosition.description else ""
                val urlToImage =
                    if (newsPosition.urlToImage != null) newsPosition.urlToImage else ""
                val content =
                    if (newsPosition.content != null) newsPosition.content else ""


                val date = newsPosition.publishedAt
                    .replace("T", " ").replace("Z", "")
                newsViewHolder.source.text = newsPosition.source.name
                newsViewHolder.date.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .parse(date)?.toString()
                newsViewHolder.title.text = newsPosition.title

                if (newsModel.exist(newsPosition.url)) {
                    newsViewHolder.bookmark.setImageDrawable(
                        ResourcesCompat
                            .getDrawable(
                                context.resources,
                                R.drawable.ic_bookmarked, null
                            )
                    )
                } else {
                    newsViewHolder.bookmark.setImageDrawable(
                        ResourcesCompat
                            .getDrawable(
                                context.resources,
                                R.drawable.ic_bookmark, null
                            )
                    )
                }
                newsViewHolder.bookmarkView.setOnClickListener {
                    if (newsModel.exist(newsPosition.url)) {
                        newsModel.deleteOne(newsPosition.url)
                        newsViewHolder.bookmark.setImageDrawable(
                            ResourcesCompat
                                .getDrawable(
                                    context.resources,
                                    R.drawable.ic_bookmark, null
                                )
                        )
                    } else {
                        newsBookMark()
                        newsModel.insert(
                            BookMarkRoom(
                                url = newsPosition.url,
                                source = newsPosition.source.name,
                                author = author,
                                title = title,
                                description = description.replace(regex = Regex("<.*?>"), ""),
                                urlToImage = urlToImage,
                                publishedAt = newsPosition.publishedAt,
                                content = content.replace(regex = Regex("<.*?>"), ""),
                            )
                        )
                        newsViewHolder.bookmark.setImageDrawable(
                            ResourcesCompat
                                .getDrawable(
                                    context.resources,
                                    R.drawable.ic_bookmarked, null
                                )
                        )
                    }
                }


                activity.runOnUiThread {
                    try {
                        Glide.with(context)
                            .load(newsPosition.urlToImage)
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    newsViewHolder.progressBar.visibility = View.GONE
                                    newsViewHolder.image.post {
                                        newsViewHolder.image.setImageDrawable(
                                            ResourcesCompat
                                                .getDrawable(
                                                    context.resources,
                                                    R.drawable.ic_undraw_page_not_found_su7k, null
                                                )
                                        )
                                    }
                                    return true
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    newsViewHolder.progressBar.visibility = View.GONE
                                    return false
                                }

                            })
                            .into(holder.image)

                    } catch (e: RuntimeException) {
                        newsViewHolder.progressBar.visibility = View.GONE
                        newsViewHolder.image.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                context.resources,
                                R.drawable.ic_undraw_page_not_found_su7k,
                                null
                            )
                        )
                    }
                }

                newsViewHolder.itemView.setOnClickListener {
                    cardClick()
                    context.startActivity(
                        Intent(context, ViewNewActivity::class.java)
                            .putExtra("url", newsPosition.url)
                            .putExtra("image", urlToImage)
                            .putExtra("title", title)
                            .putExtra(
                                "content", content
                                    .replace(regex = Regex("<.*?>"), "")
                            )
                            .putExtra(
                                "description", description
                                    .replace(regex = Regex("<.*?>"), "")
                            )
                            .putExtra("source", newsPosition.source.name)
                    )
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if ((position + 1) % 4 == 0 && (position + 1) != 1) {
            return ITEM_TYPE_BANNER_AD
        }
        return ITEM_TYPE_COUNTRY
    }

    //Banner Ad View Holder
    internal class MyAdViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!)


    internal class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.title)
        var image: ImageView = itemView.findViewById(R.id.image)
        var bookmark: ImageView = itemView.findViewById(R.id.bookmark)
        var bookmarkView: RelativeLayout = itemView.findViewById(R.id.bookmarkView)
        var source: TextView = itemView.findViewById(R.id.source)
        var date: TextView = itemView.findViewById(R.id.date)
        var progressBar: LottieAnimationView = itemView.findViewById(R.id.progressBar)
    }

    private fun cardClick() {
        AppEventsLogger.newLogger(context).logEvent("cardClick")
    }

    private fun newsBookMark() {
        AppEventsLogger.newLogger(context).logEvent("newsBookMark")
    }

}