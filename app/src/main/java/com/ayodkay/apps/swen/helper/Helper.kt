package com.ayodkay.apps.swen.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.FragmentGeneralBinding
import com.ayodkay.apps.swen.helper.adapter.AdMobRecyclerView
import com.ayodkay.apps.swen.helper.room.country.CountryDatabase
import com.ayodkay.apps.swen.helper.room.links.LinksDatabase
import com.ayodkay.apps.swen.helper.room.userlocation.LocationDatabase
import com.ayodkay.apps.swen.model.News
import com.ayodkay.apps.swen.model.NewsArticle
import com.ayodkay.apps.swen.view.AskLocation
import com.ayodkay.apps.swen.viewmodel.NewViewModel
import com.mopub.common.MoPub
import com.mopub.common.SdkConfiguration
import com.mopub.common.logging.MoPubLog
import com.mopub.nativeads.MoPubRecyclerAdapter
import com.mopub.nativeads.MoPubStaticNativeAdRenderer
import com.mopub.nativeads.RequestParameters
import com.mopub.nativeads.ViewBinder
import org.json.JSONObject
import java.util.*


object Helper {

    fun goDark(activity: Activity) {
        when (activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {

            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                activity.setTheme(R.style.AppThemeNight)
            } // Night mode is active, we're using dark theme
        }
    }

    fun getCountryDatabase(context: Context): CountryDatabase {
        return Room.databaseBuilder(
            context,
            CountryDatabase::class.java, "country"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    fun getLinksDatabase(context: Context): LinksDatabase {
        return Room.databaseBuilder(
            context, LinksDatabase::class.java, "links"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    fun getLocationDatabase(context: Context): LocationDatabase {
        return Room.databaseBuilder(
            context,
            LocationDatabase::class.java, "location"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    fun available(country: String): Boolean {
        val ac = arrayListOf(
            "ae", "ar", "at", "au", "be", "bg", "br", "ca", "ch", "cn", "co", "cu", "cz", "de",
            "eg", "fr", "gb", "gr", "hk", "hu", "id", "ie", "il", "in", "it", "jp", "kr", "lt",
            "lv", "ma", "mx", "my", "ng", "nl", "no", "nz", "ph", "pl", "pt", "ro", "rs", "ru",
            "sa", "se", "sg", "si", "sk", "th", "tr", "tw", "ua", "us", "ve", "za"
        )
        if (ac.contains(country.lowercase(Locale.ROOT))) {
            return true
        }
        return false
    }


    fun topCountries(country: String): Boolean {
        val ac = arrayListOf(
            "br", "in", "ar", "us", "ng", "de", "fr"
        )
        if (ac.contains(country.lowercase(Locale.ROOT))) {
            return true
        }
        return false
    }

    @JvmStatic
    @Deprecated("changed to retrofit")
    fun handleJson(response: JSONObject): ArrayList<News> {

        val news: ArrayList<News> = arrayListOf()

        val result = response.getJSONArray("articles")

        for (results_loop in 0 until result.length()) {
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
                )
            )
        }

        return news
    }


    @JvmOverloads
    @JvmStatic
    fun setupFragment(
        category: String? = "",
        frag: Fragment,
        binding: FragmentGeneralBinding,
        q: String? = "",
        isEverything: Boolean = false
    ): View {
        if (isEverything) {
            binding.swipeRefresh.setOnRefreshListener {
                setUpObserverEveryTime(q, binding, frag)
            }
            setUpObserverEveryTime(q, binding, frag)
        } else {
            binding.swipeRefresh.setOnRefreshListener {
                setUpObserver(category.orEmpty(), frag, binding, q)
            }
            setUpObserver(category.orEmpty(), frag, binding, q)
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @JvmStatic
    private fun setUpObserver(
        category: String,
        frag: Fragment,
        binding: FragmentGeneralBinding,
        q: String? = ""
    ) {
        val newViewModel = ViewModelProvider(frag).get(NewViewModel::class.java)
        val articleArrayList = arrayListOf<NewsArticle>()

        val activity = frag.requireActivity()

        val db = getCountryDatabase(frag.requireContext())

        var country = ""
        try {
            country = db.countryDao().getAll().country
        } catch (e: Exception) {
            frag.requireContext()
                .startActivity(Intent(frag.requireContext(), AskLocation::class.java))
            frag.requireActivity().finish()
        }

        with(newViewModel) {
            try {
                country = db.countryDao().getAll().country
            } catch (e: Exception) {
                frag.requireContext()
                    .startActivity(
                        Intent(
                            frag.requireContext(),
                            AskLocation::class.java
                        )
                    )
                frag.requireActivity().finish()
            }

            getHeadlineFromRepo(country = country, category = category, q = q, pageSize = 100)
                .observe(frag.viewLifecycleOwner, { newsResponse ->

                    if (newsResponse != null) {
                        if (newsResponse.totalResults == 0) {
                            binding.progressBar.visibility = View.GONE
                            binding.empty.visibility =
                                View.VISIBLE
                            if (newsResponse.status == "400") {
                                binding.emptyText.text = "Internet Error"
                            }
                            binding.emptyText.visibility = View.VISIBLE
                            binding.swipeText.visibility = View.VISIBLE

                            binding.newsRecyclerView.visibility =
                                View.GONE
                            binding.swipeRefresh.isRefreshing = false
                        } else {
                            binding.newsRecyclerView.visibility =
                                View.VISIBLE
                            binding.empty.visibility =
                                View.GONE
                            binding.emptyText.visibility = View.GONE
                            binding.swipeText.visibility = View.GONE

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
                                activity,
                                AdMobRecyclerView(articleArrayList, activity, frag.requireContext())
                            ).apply {
                                registerAdRenderer(moPubStaticNativeAdRenderer)
                            }.also {
                                binding.progressBar.visibility = View.GONE
                                binding.newsRecyclerView
                                    .apply {
                                        it.loadAds(
                                            frag.getString(R.string.mopub_adunit_native),
                                            requestParameters
                                        )
                                        adapter = it
                                        layoutManager = LinearLayoutManager(context)
                                        scrollToPosition(0)
                                    }
                            }
                            binding.swipeRefresh.isRefreshing = false
                        }
                    }

                })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpObserverEveryTime(
        q: String?,
        binding: FragmentGeneralBinding,
        frag: Fragment,
    ) {

        val articleArrayList = arrayListOf<NewsArticle>()
        val newViewModel = ViewModelProvider(frag).get(NewViewModel::class.java)

        val db = getCountryDatabase(frag.requireContext())
        var language = ""
        try {
            language = db.countryDao().getAll().iso
        } catch (e: Exception) {
            frag.requireContext()
                .startActivity(Intent(frag.requireContext(), AskLocation::class.java))
            frag.requireActivity().finish()
        }
        newViewModel.getEveryThingFromRepo(
            q = q,
            sort_by = "newest",
            language = language,
            pageSize = 100
        )
            .observe(frag.viewLifecycleOwner, { newsResponse ->
                if (newsResponse.totalResults == 0) {
                    binding.empty.visibility = View.VISIBLE
                    binding.emptyText.visibility = View.VISIBLE
                    binding.swipeText.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE

                    if (newsResponse.status == "400") {
                        binding.emptyText.text = "Internet Error"
                    }
                    binding.newsRecyclerView.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                } else {
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
                        frag.requireActivity(), AdMobRecyclerView(
                            articleArrayList,
                            frag,
                            frag.requireContext()
                        )
                    ).apply {
                        registerAdRenderer(moPubStaticNativeAdRenderer)
                    }.also {
                        binding.progressBar.visibility = View.GONE
                        binding.newsRecyclerView.apply {
                            adapter = it
                            layoutManager = LinearLayoutManager(frag.context)
                            it.loadAds(
                                frag.getString(R.string.mopub_adunit_native),
                                requestParameters
                            )
                        }
                    }
                    binding.swipeRefresh.isRefreshing = false
                }

            })
    }

    fun initializeAds(context: Context, adUnit: String) {
        val sdkConfiguration = SdkConfiguration.Builder(adUnit)
            .withLogLevel(MoPubLog.LogLevel.DEBUG)
            .withLegitimateInterestAllowed(false)
            .build()

        MoPub.initializeSdk(context, sdkConfiguration) { Log.d("Mopub", "SDK initialized") }
    }
}