package com.ayodkay.apps.swen.helper.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.ads.NativeTemplateStyle
import com.ayodkay.apps.swen.helper.ads.TemplateView
import com.ayodkay.apps.swen.helper.room.links.Links
import com.ayodkay.apps.swen.view.WebView
import com.google.android.gms.ads.*

private val ITEM_TYPE_COUNTRY by lazy { 0 }
private val ITEM_TYPE_BANNER_AD by lazy { 1 }
class LinksAdapter internal constructor(private val context: Context, private val links: ArrayList<Links>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val activity = context as Activity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            ITEM_TYPE_BANNER_AD->{
                val bannerLayoutView: View = LayoutInflater.from(context)
                    .inflate(R.layout.admob, parent, false)

                return MyAdViewHolder(bannerLayoutView)
            }

            else->{
                val view: View = LayoutInflater.from(context)
                    .inflate(R.layout.news_links_saved, parent, false)

                return LinksViewHolder(view)
            }


        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE_BANNER_AD -> {
                activity.runOnUiThread {
                    val progressBar: LottieAnimationView =
                        holder.itemView.findViewById(R.id.adsProgress)
                    val template: TemplateView = holder.itemView.findViewById(R.id.my_template)
                    val error: LottieAnimationView =
                        holder.itemView.findViewById(R.id.error)
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
                        .build().apply {
                            loadAd(AdRequest.Builder().build())
                        }
                }

            }

            else-> {
                val linksViewHolder = holder as LinksViewHolder
                val newsPosition = links[position]
                with(linksViewHolder){
                    if (Helper.getLinksDatabase(context).linksDao().exist(newsPosition.link)){
                        savedLinkView.visibility = View.GONE
                        unsavedLinkView.visibility = View.VISIBLE
                    }
                    savedLinkView.setOnClickListener {
                        Helper.getLinksDatabase(context).linksDao().insertAll(
                            Links(link = newsPosition.link))
                        savedLinkView.visibility = View.GONE
                        unsavedLinkView.visibility = View.VISIBLE
                    }

                    unsavedLinkView.setOnClickListener {
                        Helper.getLinksDatabase(context).linksDao().deleteOne(newsPosition.link)
                        savedLinkView.visibility = View.VISIBLE
                        unsavedLinkView.visibility = View.GONE
                    }

                    itemView.setOnClickListener {
                        context.startActivity(
                            Intent(context, WebView::class.java)
                                .putExtra("url", newsPosition.link)
                                .putExtra("toMain", false)
                        )
                    }
                }

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if ((position+1) % 4 == 0 && (position+1) != 1) {
            return ITEM_TYPE_BANNER_AD
        }
        return ITEM_TYPE_COUNTRY
    }

    override fun getItemCount(): Int {
       return links.size
    }

    //Banner Ad View Holder
    internal class MyAdViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!)

    internal class LinksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        //var link: TextView = itemView.findViewById(R.id.link)
        var savedLinkView: RelativeLayout = itemView.findViewById(R.id.saved_link_view)
        var unsavedLinkView: RelativeLayout = itemView.findViewById(R.id.saved_link_view)
    }
}