package com.ayodkay.apps.swen.view.search

import android.content.Context
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_NEUTRAL
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.FragmentSearchBinding
import com.ayodkay.apps.swen.helper.Helper.setUpNewsClient
import com.ayodkay.apps.swen.helper.constant.ErrorMessage
import com.ayodkay.apps.swen.helper.extentions.ifNull
import com.ayodkay.apps.swen.helper.room.bookmarks.BookmarkRoomVM
import com.ayodkay.apps.swen.view.BaseFragment
import com.github.ayodkay.builder.EverythingBuilder
import com.github.ayodkay.models.ArticleResponse
import com.github.ayodkay.mvvm.interfaces.ArticlesLiveDataResponseCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject

class SearchFragment : BaseFragment() {
    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentSearchBinding.inflate(inflater, container, false).apply {
        viewModel = searchViewModel
        searchViewModel.bookMarkRoom
            .set(ViewModelProvider(requireActivity())[BookmarkRoomVM::class.java])
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
                        .setTextColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.textPrimary,
                                null
                            )
                        )

                    getButton(BUTTON_POSITIVE)
                        .setTextColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.textPrimary,
                                null
                            )
                        )

                    getButton(BUTTON_NEUTRAL)
                        .setTextColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.textPrimary,
                                null
                            )
                        )
                }
        }

        searchViewModel.searchEvent.observe(viewLifecycleOwner) {
            loadNews(it)
        }

        searchViewModel.goToViewNewsFragment.observe(viewLifecycleOwner) {
            navigateTo(
                SearchFragmentDirections.actionNavMainSearchToNavViewNews(
                    source = it.source.name.ifNull { "" },
                    url = it.url.ifNull { "" },
                    image = it.urlToImage.ifNull { "" },
                    title = it.title.ifNull { "" },
                    content = it.content.ifNull { it.description.ifNull { "" } },
                    description = it.description.ifNull { "" }
                )
            )
        }
    }

    private fun loadNews(query: String?) {
        val everythingBuilder = EverythingBuilder.Builder()
            .q(query.orEmpty())
            .sortBy(searchViewModel.sort)
            .language(searchViewModel.getSelectedCountryDao.countryDao().getAll().iso)
            .pageSize(100)
            .build()

        requireActivity().currentFocus?.let { view ->
            val imm = requireActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        with(
            setUpNewsClient(
                requireActivity(),
                searchViewModel.remoteConfig.getInstance().getString("news_api_key")
            )
        ) {
            getEverything(
                everythingBuilder,
                object : ArticlesLiveDataResponseCallback {
                    override fun onFailure(throwable: Throwable) {
                        if (throwable.toString() == ErrorMessage.unknownHostException) {
                            searchViewModel.showEmpty.set(true)
                            searchViewModel.emptyTextValue.set("Internet Error")
                        }
                        val props = JSONObject()
                        props.put("source", "Search Fragment")
                        props.put("reason", throwable.toString())
                        searchViewModel.mixpanel.track("onFailure", props)
                    }

                    override fun onSuccess(response: MutableLiveData<ArticleResponse>) {
                        response.observeForever { newsResponse ->
                            if (newsResponse.totalResults == 0) {
                                searchViewModel.showEmpty.set(true)
                                searchViewModel.emptyTextValue.set(getString(R.string.not_found))
                            } else {
                                searchViewModel.showEmpty.set(false)
                                searchViewModel.showBannerAd.set(false)
                                searchViewModel.newsList.addAll(newsResponse.articles)
                            }
                        }
                    }
                }
            )
        }
    }
}
