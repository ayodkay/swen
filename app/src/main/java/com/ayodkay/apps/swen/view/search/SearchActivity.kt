package com.ayodkay.apps.swen.view.search

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.NewsApiClient
import com.ayodkay.apps.swen.helper.adapter.NewsAdapter
import com.ayodkay.apps.swen.viewmodel.NewsViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.news_list_card.*
import org.json.JSONObject


class SearchActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {

            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                setTheme(R.style.AppThemeNight)
            } // Night mode is active, we're using dark theme
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        searchBar.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                vanPersie(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                vanPersie(newText)

                return true
            }

        })
    }

    private fun vanPersie(query: String?){
        val queue = Volley.newRequestQueue(this)
        val newsViewModel: NewsViewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        val newsApiClient = NewsApiClient()
        val db = newsApiClient.getDatabase(this@SearchActivity)


        val jsonRequest = @SuppressLint("SetTextI18n")
        object : JsonObjectRequest(
            Method.GET,
            NewsApiClient.getEverything(
                newsApiClient,
                q = query,
                sort_by = "popularity",
                language = db.countryDao().getAll().iso,
                page = 3
            ),
            null,
            Response.Listener<JSONObject> {
                if (it.getInt("totalResults") == 0) {
                    empty.visibility = View.VISIBLE
                    searchRecycle.visibility = View.GONE
                    totalResults.visibility = View.GONE
                } else {
                    empty.visibility = View.GONE
                    searchRecycle.visibility = View.VISIBLE
                    totalResults.visibility = View.VISIBLE
                    totalResults.text = "${it.getInt("totalResults").toString()} ${resources.getString(R.string.articles_found)}"
                    searchRecycle.apply {
                        layoutManager = LinearLayoutManager(this@SearchActivity)
                        hasFixedSize()
                        adapter = NewsAdapter(NewsApiClient.handleJson(it), this@SearchActivity)
                    }
                }
            },
            Response.ErrorListener {

            }


        ){

        }
        queue.add(jsonRequest)
    }
}