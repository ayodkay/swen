package com.ayodkay.apps.swen.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.adapter.AdsRecyclerView
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
import io.ak1.BubbleTabBar
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
            "ae",
            "ar",
            "at",
            "au",
            "be",
            "bg",
            "br",
            "ca",
            "ch",
            "cn",
            "co",
            "cu",
            "cz",
            "de",
            "eg",
            "fr",
            "gb",
            "gr",
            "hk",
            "hu",
            "id",
            "ie",
            "il",
            "in",
            "it",
            "jp",
            "kr",
            "lt",
            "lv",
            "ma",
            "mx",
            "my",
            "ng",
            "nl",
            "no",
            "nz",
            "ph",
            "pl",
            "pt",
            "ro",
            "rs",
            "ru",
            "sa",
            "se",
            "sg",
            "si",
            "sk",
            "th",
            "tr",
            "tw",
            "ua",
            "us",
            "ve",
            "za"
        )
        if (ac.contains(country.toLowerCase(Locale.ROOT))) {
            return true
        }
        return false
    }


    fun topCountries(country: String): Boolean {
        val ac = arrayListOf(
            "br", "in", "ar", "us", "ng", "de", "fr"
        )
        if (ac.contains(country.toLowerCase(Locale.ROOT))) {
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


    @JvmStatic
    fun setupFragment(
        category: String, frag: Fragment, inflater: LayoutInflater,
        container: ViewGroup?, q: String? = "", childFragmentManager: FragmentManager
    ): View? {


        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val refresh = root.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)

        setUpObserver(category, q, frag, root, childFragmentManager)

        refresh.setOnRefreshListener {
            setUpObserver(category, q, frag, root, childFragmentManager)
        }

        return root
    }

    @SuppressLint("SetTextI18n")
    private fun setUpObserver(
        category: String,
        q: String?,
        frag: Fragment,
        root: View,
        childFragmentManager: FragmentManager
    ) {

        val newViewModel = ViewModelProvider(frag).get(NewViewModel::class.java)
        val articleArrayList = arrayListOf<NewsArticle>()

        val refresh = root.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val emptyText = root.findViewById<TextView>(R.id.emptyText)
        val swipeText = root.findViewById<TextView>(R.id.swipeText)

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
                    .startActivity(Intent(frag.requireContext(), AskLocation::class.java))
                frag.requireActivity().finish()
            }

            getHeadlineFromRepo(country = country, q = q, category = category, pageSize = 100)
                .observe(frag, { newsResponse ->

                    if (newsResponse != null) {
                        if (newsResponse.totalResults == 0) {
                            root.findViewById<ImageView>(R.id.empty).visibility = View.VISIBLE
                            if (newsResponse.status == "400") {
                                emptyText.text = "Internet Error"
                            }
                            emptyText.visibility = View.VISIBLE
                            swipeText.visibility = View.VISIBLE

                            root.findViewById<RecyclerView>(R.id.newsRecyclerView).visibility =
                                View.GONE
                            refresh.isRefreshing = false
                        } else {
                            root.findViewById<RecyclerView>(R.id.newsRecyclerView).visibility =
                                View.VISIBLE
                            root.findViewById<ImageView>(R.id.empty).visibility = View.GONE
                            emptyText.visibility = View.GONE
                            swipeText.visibility = View.GONE

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
                                frag.requireActivity(), AdsRecyclerView(
                                    articleArrayList,
                                    frag,
                                    frag.requireContext()
                                )
                            ).apply {
                                registerAdRenderer(moPubStaticNativeAdRenderer)
                            }.also {
                                root.findViewById<RecyclerView>(R.id.newsRecyclerView).apply {
                                    adapter = it
                                    layoutManager = LinearLayoutManager(frag.context)
                                    it.loadAds(
                                        frag.getString(R.string.mopub_adunit_native),
                                        requestParameters
                                    )
                                }
                            }
                            refresh.isRefreshing = false
                        }
                    }

                })
        }
    }


    @JvmStatic
    fun setupEveryThingFragment(
        frag: Fragment, inflater: LayoutInflater,
        container: ViewGroup?, q: String? = "", childFragmentManager: FragmentManager
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val refresh = root.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)

        setUpObserverEveryTime(q, root, frag, childFragmentManager)

        refresh.setOnRefreshListener {
            setUpObserverEveryTime(q, root, frag, childFragmentManager)
        }
        return root
    }

    @SuppressLint("SetTextI18n")
    private fun setUpObserverEveryTime(
        q: String?,
        root: View,
        frag: Fragment,
        childFragmentManager: FragmentManager
    ) {

        val articleArrayList = arrayListOf<NewsArticle>()
        val newViewModel = ViewModelProvider(frag).get(NewViewModel::class.java)
        val refresh = root.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val emptyText = root.findViewById<TextView>(R.id.emptyText)
        val swipeText = root.findViewById<TextView>(R.id.swipeText)

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
            .observe(frag, { newsResponse ->
                if (newsResponse.totalResults == 0) {
                    root.findViewById<ImageView>(R.id.empty).visibility = View.VISIBLE
                    emptyText.visibility = View.VISIBLE
                    swipeText.visibility = View.VISIBLE

                    if (newsResponse.status == "400") {
                        emptyText.text = "Internet Error"
                    }
                    root.findViewById<RecyclerView>(R.id.newsRecyclerView).visibility = View.GONE
                    refresh.isRefreshing = false
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
                        frag.requireActivity(), AdsRecyclerView(
                            articleArrayList,
                            frag,
                            frag.requireContext()
                        )
                    ).apply {
                        registerAdRenderer(moPubStaticNativeAdRenderer)
                    }.also {
                        root.findViewById<RecyclerView>(R.id.newsRecyclerView).apply {
                            adapter = it
                            layoutManager = LinearLayoutManager(frag.context)
                            it.loadAds(
                                frag.getString(R.string.mopub_adunit_native),
                                requestParameters
                            )
                        }
                    }
                    refresh.isRefreshing = false
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


    private fun BubbleTabBar.onNavDestinationSelected(
        itemId: Int,
        navController: NavController
    ): Boolean {
        val builder = NavOptions.Builder()
            .setLaunchSingleTop(true)
        if (navController.currentDestination!!.parent!!.findNode(itemId) is ActivityNavigator.Destination) {
            builder.setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
        } else {
            builder.setEnterAnim(R.animator.nav_default_enter_anim)
                .setExitAnim(R.animator.nav_default_exit_anim)
                .setPopEnterAnim(R.animator.nav_default_pop_enter_anim)
                .setPopExitAnim(R.animator.nav_default_pop_exit_anim)
        }
        //if (itemId == getChildAt(0).id) {
        //builder.setPopUpTo(findStartDestination(navController.graph)!!.id, true)
        // }
        builder.setPopUpTo(itemId, true)
        val options = builder.build()
        return try {
            //TODO provide proper API instead of using Exceptions as Control-Flow.
            navController.navigate(itemId, null, options)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}