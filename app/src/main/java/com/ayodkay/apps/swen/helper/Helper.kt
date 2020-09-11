package com.ayodkay.apps.swen.helper

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.adapter.AdsRecyclerView
import com.ayodkay.apps.swen.helper.room.country.AppDatabase
import com.ayodkay.apps.swen.model.News
import com.ayodkay.apps.swen.viewmodel.NewsViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import org.json.JSONObject
import java.util.ArrayList

object Helper{

    fun getDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "country"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    @JvmStatic
    fun handleJson(response: JSONObject): ArrayList<News> {

        val news: ArrayList<News> = arrayListOf()

        val result = response.getJSONArray("articles")

        for (results_loop in 0 until result.length()){
            val results = result.getJSONObject(results_loop)

            val source = results.getJSONObject("source")
            news.add(
                News(
                    source.getString("name"),
                    results.getString("author"),
                    results.getString("title"),
                    results.getString("description"),
                    results.getString("url"),
                    results.getString("urlToImage"),
                    results.getString("publishedAt"),
                    results.getString("content"),
                    results.getString("publishedAt")
                )
            )
        }

        return news
    }


    @SuppressLint("SetTextI18n")
    @JvmStatic
    fun setupFragment(category: String, frag: Fragment, inflater: LayoutInflater,
                      container: ViewGroup?, q: String?=""): View?{

        val newsViewModel: NewsViewModel = ViewModelProvider(frag).get(NewsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_main, container, false)

        val adFrag = root.findViewById<AdView>(R.id.adFrag)

        val newsApiClient = NewsApiClient()

        val db = getDatabase(frag.requireContext())

        MobileAds.initialize(frag.context)
        val adRequest = AdRequest.Builder().build()
        adFrag.loadAd(adRequest)

        newsViewModel.getNews(
            NewsApiClient.getTopHeadline(
                newsApiClient,
                country = db.countryDao().getAll().country,
                q = q,
                category = category,
                pageSize = 100
            )
        ).observe(frag.viewLifecycleOwner, Observer {
            if (it.getInt("totalResults") == 0) {
                root.findViewById<ImageView>(R.id.empty).visibility = View.VISIBLE
                root.findViewById<TextView>(R.id.emptyText).visibility = View.VISIBLE
                root.findViewById<RecyclerView>(R.id.newsRecyclerView).visibility = View.GONE
            } else {
                root.findViewById<TextView>(R.id.totalResults).text = "${it.getInt("totalResults")} ${frag.resources.getString(
                    R.string.articles_found) }"

                root.findViewById<RecyclerView>(R.id.newsRecyclerView).apply {
                    layoutManager = LinearLayoutManager(frag.context)
                    hasFixedSize()
                    adapter = AdsRecyclerView(handleJson(it),frag ,frag.viewLifecycleOwner,frag.requireContext())
                }
            }
        })
        return root
    }


    @SuppressLint("SetTextI18n")
    @JvmStatic
    fun setupEveryThingFragment(frag: Fragment, inflater: LayoutInflater,
                                container: ViewGroup?,q: String?=""): View?{

        val newsViewModel: NewsViewModel = ViewModelProvider(frag).get(NewsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val adFrag = root.findViewById<AdView>(R.id.adFrag)
        val newsApiClient = NewsApiClient()

        MobileAds.initialize(frag.context)
        val adRequest = AdRequest.Builder().build()
        adFrag.loadAd(adRequest)

        val db = getDatabase(frag.requireContext())

        newsViewModel.getNews(
            NewsApiClient.getEverything(
                newsApiClient,
                q = q,
                sort_by = "newest",
                language = db.countryDao().getAll().iso,
                pageSize = 100
            )
        ).observe(frag.viewLifecycleOwner, Observer {
            if (it.getInt("totalResults") == 0) {
                root.findViewById<ImageView>(R.id.empty).visibility = View.VISIBLE
                root.findViewById<TextView>(R.id.emptyText).visibility = View.VISIBLE
                root.findViewById<RecyclerView>(R.id.newsRecyclerView).visibility = View.GONE
            } else {
                val getResult = handleJson(it)
                root.findViewById<TextView>(R.id.totalResults).text = "${getResult.size} ${frag.resources.getString(R.string.articles_found) }"

                root.findViewById<RecyclerView>(R.id.newsRecyclerView).apply {
                    layoutManager = LinearLayoutManager(frag.context)
                    hasFixedSize()
                    adapter = AdsRecyclerView(getResult,frag, frag.viewLifecycleOwner, frag.requireContext())
                }
            }
        })
        return root
    }
}