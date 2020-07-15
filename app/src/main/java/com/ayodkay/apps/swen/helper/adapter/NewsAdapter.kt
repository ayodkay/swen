package com.ayodkay.apps.swen.helper.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.model.News
import com.ayodkay.apps.swen.view.ViewNewActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


class NewsAdapter
internal constructor(private val newsList: ArrayList<News>,private val context: Context):
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>(){

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

        try {
            Picasso.get().load(userPosition.urlToImage).into(holder.image, object : Callback {
                override fun onSuccess() {
                    holder.progressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    holder.progressBar.visibility = View.GONE
                    holder.image.setImageDrawable(context.resources.getDrawable(R.drawable.ic_undraw_page_not_found_su7k))
                }


            })
        }catch (e:Exception){
            holder.progressBar.visibility = View.GONE
            holder.image.setImageDrawable(context.resources.getDrawable(R.drawable.ic_undraw_page_not_found_su7k))
        }


        holder.itemView.setOnClickListener {

            context.startActivity(Intent(context,ViewNewActivity::class.java)
                .putExtra("url",userPosition.url)
                .putExtra("image",userPosition.urlToImage)
                .putExtra("title",userPosition.title)
                .putExtra("content",userPosition.content)
                .putExtra("description",userPosition.description)
                .putExtra("source",userPosition.source)
            )
        }
    }

}