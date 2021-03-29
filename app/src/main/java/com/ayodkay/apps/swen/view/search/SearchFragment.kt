package com.ayodkay.apps.swen.view.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.adapter.AdsRecyclerView
import com.ayodkay.apps.swen.model.NewsArticle
import com.ayodkay.apps.swen.viewmodel.NewViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_search.*

class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)

        val adView = rootView.findViewById<AdView>(R.id.adView)
        MobileAds.initialize(requireContext())
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)



        return rootView

    }


    @SuppressLint("SetTextI18n")
    fun loadNews(query: String?) {
        val db = Helper.getCountryDatabase(requireContext())
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