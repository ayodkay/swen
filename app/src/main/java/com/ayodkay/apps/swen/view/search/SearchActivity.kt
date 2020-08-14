package com.ayodkay.apps.swen.view.search

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.AppLog
import com.ayodkay.apps.swen.helper.NewsApiClient
import com.ayodkay.apps.swen.helper.adapter.AdsRecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_search.*
import org.json.JSONObject


class SearchActivity : AppCompatActivity() {

     var queryValue: String = "null"
    lateinit var sort:String


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val map: HashMap<String, String> = hashMapOf("popularity" to getString(R.string.popularity),
            "newest" to getString(R.string.newest),
            "relevancy" to getString(R.string.relevancy))

        val singleItems = arrayOf(map["popularity"], map["newest"], map["relevancy"])
        var checkedItem = 0

        sort = singleItems[checkedItem]!!

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        searchBar.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                queryValue = query.toString()
                vanPersie(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                queryValue = newText.toString()
                vanPersie(newText)

                return true
            }

        })

        sortBy.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.sort_news))
                .setNeutralButton(resources.getString(android.R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                    if (queryValue != "null"){
                        vanPersie(queryValue)
                    }

                }
                // Single-choice items (initialized with checked item)
                .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                    sort =  singleItems[which]!!
                    checkedItem = which
                }
                .show()
        }
    }

    private fun vanPersie(query: String?){
        val queue = Volley.newRequestQueue(this)
        val newsApiClient = NewsApiClient()
        val db = newsApiClient.getDatabase(this@SearchActivity)

        val jsonRequest = @SuppressLint("SetTextI18n")
        object : JsonObjectRequest(
            Method.GET,
            NewsApiClient.getEverything(
                newsApiClient,
                q = query,
                sort_by = sort,
                language = db.countryDao().getAll().iso,
                page = 4
            ),
            null,
            Response.Listener<JSONObject> {

                if (it.getInt("totalResults") == 0) {
                    empty.visibility = View.VISIBLE
                    searchRecycle.visibility = View.GONE
                    totalResults.visibility = View.GONE
                } else {
                    val getResult  = NewsApiClient.handleJson(it)
                    empty.visibility = View.GONE
                    searchRecycle.visibility = View.VISIBLE
                    totalResults.visibility = View.VISIBLE
                    totalResults.text = "${getResult.size} ${resources.getString(R.string.articles_found)}"
                    searchRecycle.apply {
                        layoutManager = LinearLayoutManager(this@SearchActivity)
                        hasFixedSize()
                        adapter = AdsRecyclerView(getResult, this@SearchActivity,this@SearchActivity,this@SearchActivity)
                    }
                }
            },
            Response.ErrorListener {
                AppLog.log("search", it)
            }


        ){

        }
        queue.add(jsonRequest)
    }

}