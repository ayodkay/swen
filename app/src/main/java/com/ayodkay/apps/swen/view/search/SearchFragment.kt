package com.ayodkay.apps.swen.view.search

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.ActivitySearchBinding
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.adapter.AdsRecyclerView
import com.ayodkay.apps.swen.model.NewsArticle
import com.ayodkay.apps.swen.viewmodel.NewViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mopub.nativeads.MoPubRecyclerAdapter
import com.mopub.nativeads.MoPubStaticNativeAdRenderer
import com.mopub.nativeads.RequestParameters
import com.mopub.nativeads.ViewBinder
import java.util.*

class SearchFragment : Fragment() {

    var queryValue: String = "null"
    lateinit var sort: String
    private var sortOptions = arrayListOf("popularity", "publishedAt", "relevancy")

    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivitySearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val singleSort = arrayOf(
            getString(R.string.popularity),
            getString(R.string.newest),
            getString(R.string.relevancy)
        )
        var checkedSort = 1
        sort = sortOptions[checkedSort]

//        MobileAds.initialize(requireContext())
//        val adRequest = AdRequest.Builder().build()
//        binding.adView.loadAd(adRequest)

        binding.bannerMopubview.apply {
            setAdUnitId(getString(R.string.mopub_adunit_banner))
            loadAd()
        }

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                queryValue = query.toString()
                loadNews(queryValue)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.bannerMopubview.visibility = View.VISIBLE
                return false
            }

        })

        binding.sortBy.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
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
        val db = Helper.getCountryDatabase(requireContext())
        val newViewModel = ViewModelProvider(this).get(NewViewModel::class.java)
        val articleArrayList = arrayListOf<NewsArticle>()

        newViewModel.getEveryThingFromRepo(
            q = query, sort_by = sort,
            language = db.countryDao().getAll().iso, pageSize = 100
        ).observe(requireActivity(), { newsResponse ->

            requireActivity().currentFocus?.let { view ->
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
            if (newsResponse.totalResults == 0) {
                binding.empty.visibility = View.VISIBLE
                binding.searchRecycle.visibility = View.GONE
                binding.totalResults.visibility = View.GONE
            } else {
                binding.empty.visibility = View.GONE
                binding.searchRecycle.visibility = View.VISIBLE
                binding.totalResults.visibility = View.VISIBLE
                binding.totalResults.text =
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
                    requireActivity(), AdsRecyclerView(
                        articleArrayList,
                        requireActivity(),
                        requireActivity()
                    )
                ).apply {
                    registerAdRenderer(moPubStaticNativeAdRenderer)
                }.also {
                    binding.bannerMopubview.visibility = View.GONE
                    binding.searchRecycle.apply {
                        adapter = it
                        layoutManager = LinearLayoutManager(requireActivity())
                        it.loadAds(getString(R.string.mopub_adunit_native), requestParameters)
                    }
                }
            }
        })
    }
}