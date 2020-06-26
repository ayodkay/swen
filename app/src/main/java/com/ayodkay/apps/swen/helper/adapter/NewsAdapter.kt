package com.ayodkay.apps.swen.helper.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.model.News
import com.ayodkay.apps.swen.view.WebView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


class NewsAdapter internal constructor(private val newsList: ArrayList<News>,private val context: Context):  RecyclerView.Adapter<NewsAdapter.NewsViewHolder>(){

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var title: TextView = itemView.findViewById(R.id.title)
        var image: ImageView = itemView.findViewById(R.id.image)
        var description: TextView = itemView.findViewById(R.id.description)
        var source: TextView = itemView.findViewById(R.id.source)
        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.news_list_card, parent, false)
        return NewsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

        val userPosition = newsList[position]
        holder.source.text = userPosition.source
        holder.title.text = userPosition.title
        holder.description.text = userPosition.description

        Picasso.get().load(userPosition.urlToImage).into(holder.image, object : Callback {
            override fun onSuccess() {
                holder.progressBar.visibility = View.GONE
            }

            override fun onError(e: Exception?) {
                holder.progressBar.visibility = View.GONE
                holder.image.setImageDrawable(context.resources.getDrawable(R.drawable.ic_undraw_page_not_found_su7k))
            }


        })


        holder.itemView.setOnClickListener {

            MobileAds.initialize(context)
            val adRequest = AdRequest.Builder().build()


            // create an alert builder
            // create an alert builder
            val builder =
                AlertDialog.Builder(context)

            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val customLayout: View = inflater.inflate(R.layout.news_card, null)
            builder.setView(customLayout)
            val image = customLayout.findViewById<ImageView>(R.id.dImage)
            val content = customLayout.findViewById<TextView>(R.id.content)
            val title = customLayout.findViewById<TextView>(R.id.dTitle)
            val adView = customLayout.findViewById<AdView>(R.id.adView)
            val source = customLayout.findViewById<TextView>(R.id.dSource)

            adView.loadAd(adRequest)

            content.text = userPosition.content

            if (userPosition.content == null){
                content.text = userPosition.description
            }
            title.text = userPosition.title
            source.text = userPosition.source

            Picasso.get().load(userPosition.urlToImage).into(image, object : Callback {
                override fun onSuccess() {
                    holder.progressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    image.setImageDrawable(context.resources.getDrawable(R.drawable.ic_undraw_page_not_found_su7k))
                }

            })

            builder.setPositiveButton(
                "view full article"
            ) { _, _ -> // send data from the AlertDialog to the Activity
                context.startActivity(Intent(context,WebView::class.java).putExtra("url",userPosition.url))

            }

            builder.setNegativeButton(
                "cancel"
            ){
                    dialogInterface, _ ->

                dialogInterface.dismiss()
            }
            // create and show the alert dialog
            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()
        }
    }

}