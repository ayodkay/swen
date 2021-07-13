package com.ayodkay.apps.swen.view.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.adapter.AdsRecyclerView
import com.ayodkay.apps.swen.model.NewsArticle
import com.ayodkay.apps.swen.viewmodel.NewViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mopub.nativeads.MoPubRecyclerAdapter
import com.mopub.nativeads.MoPubStaticNativeAdRenderer
import com.mopub.nativeads.RequestParameters
import com.mopub.nativeads.ViewBinder
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*

@Deprecated("changed to fragment")
class SearchActivity : AppCompatActivity() {

    var queryValue: String = "null"
    lateinit var sort: String
    private var sortOptions = arrayListOf("popularity", "publishedAt", "relevancy")


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

        val singleSort = arrayOf(
            getString(R.string.popularity),
            getString(R.string.newest),
            getString(R.string.relevancy)
        )
        var checkedSort = 1
        sort = sortOptions[checkedSort]

//        MobileAds.initialize(this)
//        val adRequest = AdRequest.Builder().build()
//        adView.loadAd(adRequest)

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                    if (queryValue != "null") {
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

            this.currentFocus?.let { view ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
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

                articleArrayList.addAll(newsResponse.articles)
                val desiredAssets = EnumSet.of(
                    RequestParameters.NativeAdAsset.TITLE,
                    RequestParameters.NativeAdAsset.TEXT,
                    RequestParameters.NativeAdAsset.ICON_IMAGE,
                    RequestParameters.NativeAdAsset.MAIN_IMAGE,
                    RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT,
                    RequestParameters.NativeAdAsset.SPONSORED
                )
                val requestParameters = RequestParameters.Builder()
                    .desiredAssets(desiredAssets)
                    .build()

                val moPubStaticNativeAdRenderer = MoPubStaticNativeAdRenderer(
                    ViewBinder.Builder(R.layout.native_ad_list_item)
                        .titleId(R.id.native_title)
                        .textId(R.id.native_text)
                        .mainImageId(R.id.native_main_image)
                        .iconImageId(R.id.native_icon_image)
                        .callToActionId(R.id.native_cta)
                        .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                        .sponsoredTextId(R.id.native_sponsored_text_view)
                        .build()
                )

                MoPubRecyclerAdapter(
                    this@SearchActivity, AdsRecyclerView(
                        articleArrayList,
                        this@SearchActivity,
                        this@SearchActivity
                    )
                ).apply {
                    registerAdRenderer(moPubStaticNativeAdRenderer)
                }.also {
                    searchRecycle.apply {
                        adapter = it
                        layoutManager = LinearLayoutManager(this@SearchActivity)
                        it.loadAds(getString(R.string.mopub_adunit_native), requestParameters)
                    }
                }
            }
        })
    }


}