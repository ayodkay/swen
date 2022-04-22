package com.ayodkay.apps.swen.view.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.ActivitySearchBinding
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.Helper.setUpNewsClient
import com.ayodkay.apps.swen.helper.adapter.MaxAdsRecyclerView
import com.ayodkay.apps.swen.helper.constant.ErrorMessage
import com.github.ayodkay.builder.EverythingBuilder
import com.github.ayodkay.models.Article
import com.github.ayodkay.models.ArticleResponse
import com.github.ayodkay.mvvm.interfaces.ArticlesLiveDataResponseCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SearchFragment : Fragment(), MaxAdViewAdListener {

    private lateinit var nativeAdLoader: MaxNativeAdLoader


    private var nativeAd: MaxAd? = null

    var queryValue: String = "null"
    lateinit var sort: String
    private var sortOptions = arrayListOf("popularity", "publishedAt", "relevancy")

    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        nativeAdLoader =
            MaxNativeAdLoader("08f93b640def0007", context)
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


        binding.maxAdviewBanner.apply {
            loadAd()
            startAutoRefresh()
        }

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                queryValue = query.toString()
                loadNews(queryValue)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.maxAdviewBanner.visibility = View.VISIBLE
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
                .show().apply {
                    getButton(BUTTON_NEGATIVE)
                        .setTextColor(ResourcesCompat.getColor(resources,
                            R.color.textPrimary,
                            null))

                    getButton(BUTTON_POSITIVE)
                        .setTextColor(ResourcesCompat.getColor(resources,
                            R.color.textPrimary,
                            null))

                    getButton(BUTTON_NEUTRAL)
                        .setTextColor(ResourcesCompat.getColor(resources,
                            R.color.textPrimary,
                            null))
                }
        }

    }


    @SuppressLint("SetTextI18n")
    fun loadNews(query: String?) {
        val db = Helper.getCountryDatabase(requireContext())
        val newsResponseList = arrayListOf<Article>()
        val everythingBuilder = EverythingBuilder.Builder()
            .q(query.orEmpty())
            .sortBy(sort)
            .language(db.countryDao().getAll().iso)
            .pageSize(100)
            .build()
        with(setUpNewsClient(requireActivity())) {
            getEverything(everythingBuilder,
                object : ArticlesLiveDataResponseCallback {
                    override fun onFailure(throwable: Throwable) {
                        if (throwable.toString() == ErrorMessage.unknownHostException) {
                            binding.empty.visibility = View.VISIBLE
                            binding.searchRecycle.visibility = View.GONE
                            binding.emptyText.visibility = View.VISIBLE
                            binding.emptyText.text = "Internet Error"
                            val imm =
                                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.hideSoftInputFromWindow(view?.windowToken, 0)
                        }
                    }

                    override fun onSuccess(response: MutableLiveData<ArticleResponse>) {
                        response.observe(viewLifecycleOwner) { newsResponse ->
                            requireActivity().currentFocus?.let { view ->
                                val imm =
                                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                imm?.hideSoftInputFromWindow(view.windowToken, 0)

                                if (newsResponse.totalResults == 0) {
                                    binding.empty.visibility = View.VISIBLE
                                    binding.searchRecycle.visibility = View.GONE
                                    binding.emptyText.visibility = View.VISIBLE
                                } else {
                                    binding.empty.visibility = View.GONE
                                    binding.searchRecycle.visibility = View.VISIBLE
                                    binding.emptyText.visibility = View.GONE

                                    newsResponseList.addAll(newsResponse.articles)
                                    binding.maxAdviewBanner.visibility = View.GONE
                                    binding.searchRecycle.apply {
                                        adapter = MaxAdsRecyclerView(newsResponseList,
                                            this@SearchFragment,
                                            requireContext(), nativeAdLoader, nativeAd)
                                        layoutManager = LinearLayoutManager(requireActivity())
                                    }
                                }
                            }
                        }
                    }

                })
        }
    }

    override fun onDestroy() {
        // Must destroy native ad or else there will be memory leaks.
        if (nativeAd != null) {
            // Call destroy on the native ad from any native ad loader.
            nativeAdLoader.destroy(nativeAd)
        }

        // Destroy the actual loader itself
        nativeAdLoader.destroy()

        super.onDestroy()
    }

    override fun onAdLoaded(maxAd: MaxAd) {}
    override fun onAdDisplayed(ad: MaxAd?) {}
    override fun onAdHidden(ad: MaxAd?) {}
    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {}
    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {}
    override fun onAdClicked(maxAd: MaxAd) {}
    override fun onAdExpanded(maxAd: MaxAd) {}
    override fun onAdCollapsed(maxAd: MaxAd) {}
}