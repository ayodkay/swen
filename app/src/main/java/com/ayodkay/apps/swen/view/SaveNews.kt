package com.ayodkay.apps.swen.view

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.adapter.AdsRecyclerView
import com.ayodkay.apps.swen.helper.room.news.NewsRoomVM
import com.ayodkay.apps.swen.model.News
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_save_news.*
import java.util.*

class SaveNews : AppCompatActivity() {

    override fun onStart() {
        super.onStart()

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {

            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                setTheme(R.style.AppThemeNight)
            } // Night mode is active, we're using dark theme
        }
    }
    private lateinit var newsModel: NewsRoomVM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_news)

        val news: ArrayList<News> = arrayListOf()
        newsModel = ViewModelProvider(this).get(NewsRoomVM::class.java)
        var add = true

        newsModel.allNewsRoom.observe(this, Observer {
            if (it.isEmpty()){
                no_saved.visibility = View.VISIBLE
                saved_recycle.visibility = View.GONE
            }else{
                for (i in it.indices){
                    if (add){
                        news.add(News(
                            it[i].source,
                            it[i].author,
                            it[i].title,
                            it[i].description,
                            it[i].url,
                            it[i].urlToImage,
                            it[i].publishedAt,
                            it[i].content,
                            it[i].date
                        ))

                    }
                }
                add = false
            }

            saved_recycle.apply {
                layoutManager = LinearLayoutManager(this@SaveNews)
                adapter = AdsRecyclerView(news,this@SaveNews,this@SaveNews,this@SaveNews)
            }
        })

    }
}