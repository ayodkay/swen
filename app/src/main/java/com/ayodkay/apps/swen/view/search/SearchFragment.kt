package com.ayodkay.apps.swen.view.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.ayodkay.apps.swen.MainControlDirections
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.ActivitySearchBinding
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.Helper.setUpNewsClient
import com.ayodkay.apps.swen.helper.constant.ErrorMessage
import com.ayodkay.apps.swen.helper.room.bookmarks.BookmarkRoomVM
import com.github.ayodkay.builder.EverythingBuilder
import com.github.ayodkay.models.ArticleResponse
import com.github.ayodkay.mvvm.interfaces.ArticlesLiveDataResponseCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SearchFragment : Fragment() {
    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ActivitySearchBinding.inflate(inflater, container, false).apply {
        viewModel = searchViewModel
        searchViewModel.nativeAdLoader = MaxNativeAdLoader("08f93b640def0007", context)
        searchViewModel.bookMarkRoom.set(ViewModelProvider(this@SearchFragment)[BookmarkRoomVM::class.java])
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchViewModel.loadAd.set(true)

        val singleSort = arrayOf(
            getString(R.string.popularity),
            getString(R.string.newest),
            getString(R.string.relevancy)
        )
        searchViewModel.sort = searchViewModel.sortOptions[searchViewModel.checkedSort]

        searchViewModel.sortEvent.observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.sort_news))
                .setNeutralButton(resources.getString(android.R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                    if (searchViewModel.query.get() != "") {
                        loadNews(searchViewModel.query.get())
                    }

                }
                // Single-choice items (initialized with checked item)
                .setSingleChoiceItems(singleSort, searchViewModel.checkedSort) { _, which ->
                    searchViewModel.sort = searchViewModel.sortOptions[which]
                    searchViewModel.checkedSort = which
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

        searchViewModel.searchEvent.observe(viewLifecycleOwner) {
            loadNews(it)
        }

        searchViewModel.goToViewNewsFragment.observe(viewLifecycleOwner) {
            findNavController().navigate(MainControlDirections.actionToViewNews(
                source = it.source.name, url = it.url, image = it.urlToImage, title = it.title,
                content = it.content, description = it.description
            ))
        }
    }


    @SuppressLint("SetTextI18n")
    fun loadNews(query: String?) {
        val db = Helper.getCountryDatabase(requireContext())
        val everythingBuilder = EverythingBuilder.Builder()
            .q(query.orEmpty())
            .sortBy(searchViewModel.sort)
            .language(db.countryDao().getAll().iso)
            .pageSize(100)
            .build()

        with(setUpNewsClient(requireActivity())) {
            getEverything(everythingBuilder,
                object : ArticlesLiveDataResponseCallback {
                    override fun onFailure(throwable: Throwable) {
                        if (throwable.toString() == ErrorMessage.unknownHostException) {
                            searchViewModel.showEmpty.set(true)
                            searchViewModel.emptyTextValue.set("Internet Error")
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
                                    searchViewModel.showEmpty.set(true)
                                    searchViewModel.emptyTextValue.set(getString(R.string.not_found))
                                } else {
                                    searchViewModel.showEmpty.set(false)
                                    searchViewModel.hideBannerAd.set(true)
                                    searchViewModel.newsList.addAll(newsResponse.articles)
                                }
                            }
                        }
                    }

                })
        }
    }

    override fun onDestroy() {
        // Must destroy native ad or else there will be memory leaks.
        if (searchViewModel.nativeAd != null) {
            // Call destroy on the native ad from any native ad loader.
            searchViewModel.nativeAdLoader.destroy(searchViewModel.nativeAd)
        }

        // Destroy the actual loader itself
        searchViewModel.nativeAdLoader.destroy()

        super.onDestroy()
    }
}