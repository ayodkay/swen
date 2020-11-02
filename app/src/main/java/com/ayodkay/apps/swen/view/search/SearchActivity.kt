package com.ayodkay.apps.swen.view.search

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.adapter.AdsRecyclerView
import com.ayodkay.apps.swen.model.NewsArticle
import com.ayodkay.apps.swen.viewmodel.NewViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity() {

    var queryValue: String = "null"
    lateinit var sort : String
    private var sortOptions = arrayListOf("popularity","publishedAt","relevancy")




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

        val singleSort = arrayOf(getString(R.string.popularity), getString(R.string.newest), getString(R.string.relevancy))
        var checkedSort = 1
        sort = sortOptions[checkedSort]

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        searchBar.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                queryValue = query.toString()
                loadNews(queryValue)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
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
                        loadNews(queryValue)
                    }

                }
                // Single-choice items (initialized with checked item)
                .setSingleChoiceItems(singleSort, checkedSort) { _, which ->
                    sort = sortOptions[which]
                    checkedSort = which
                }
                .show()
        }
    }

    @SuppressLint("SetTextI18n")
    fun loadNews(query: String?) {
        val db = Helper.getCountryDatabase(this@SearchActivity)
        val newViewModel = ViewModelProvider(this).get(NewViewModel::class.java)
        val articleArrayList = arrayListOf<NewsArticle>()


        newViewModel.getEveryThingFromRepo(
            q = query, sort_by = sort,
            language = db.countryDao().getAll().iso, pageSize = 100
        ).observe(this, { newsResponse ->
                if (newsResponse.totalResults == 0) {
                    empty.visibility = View.VISIBLE
                    searchRecycle.visibility = View.GONE
                    totalResults.visibility = View.GONE
                } else {
                    empty.visibility = View.GONE
                    searchRecycle.visibility = View.VISIBLE
                    totalResults.visibility = View.VISIBLE
                    totalResults.text =
                        "${newsResponse.totalResults} ${resources.getString(R.string.articles_found)}"
                    searchRecycle.apply {
                        layoutManager = LinearLayoutManager(this@SearchActivity)
                        articleArrayList.addAll(newsResponse.articles)
                        hasFixedSize()
                        adapter = AdsRecyclerView(
                            articleArrayList,
                            this@SearchActivity,
                            this@SearchActivity
                        )
                    }
                }
            })
    }


}