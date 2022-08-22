package com.ayodkay.apps.swen.view.home.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ayodkay.apps.swen.MainControlDirections
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.FragmentCategoryBinding
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.constant.ErrorMessage
import com.ayodkay.apps.swen.helper.extentions.ifNull
import com.ayodkay.apps.swen.helper.room.bookmarks.BookmarkRoomVM
import com.ayodkay.apps.swen.view.BaseFragment
import com.github.ayodkay.builder.EverythingBuilder
import com.github.ayodkay.builder.TopHeadlinesBuilder
import com.github.ayodkay.models.ArticleResponse
import com.github.ayodkay.mvvm.interfaces.ArticlesLiveDataResponseCallback
import org.json.JSONObject

class CategoryFragment : BaseFragment() {
    private val args: CategoryFragmentArgs by navArgs()
    private val categoryViewModel: CategoryViewModel by viewModels()
    private lateinit var binding: FragmentCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentCategoryBinding.inflate(inflater, container, false).apply {
        viewModel = categoryViewModel
        with(categoryViewModel) {
            category = args.category
            q = args.q
            bookMarkRoom.set(ViewModelProvider(this@CategoryFragment)[BookmarkRoomVM::class.java])
        }
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val props = JSONObject().put("source", args.category)
        categoryViewModel.mixpanel.track("Category Instance", props)
        try {
            with(categoryViewModel.getSelectedCountryDao.countryDao().getAll()) {
                categoryViewModel.country = country
                categoryViewModel.language = iso
            }
        } catch (e: Exception) {
            navigateTo(R.id.nav_location)
        }

        binding.swipeRefresh.setOnRefreshListener {
            loadNews()
        }
        loadNews()
        categoryViewModel.goToViewNewsFragment.observe(viewLifecycleOwner) {
            navigateTo(
                MainControlDirections.actionToViewNews(
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

    private fun loadNews() {
        with(
            Helper.setUpNewsClient(
                requireActivity(),
                categoryViewModel.remoteConfig.getInstance().getString("news_api_key")
            )
        ) {
            if (categoryViewModel.category.isEmpty()) {
                val everythingBuilder = EverythingBuilder.Builder()
                    .q(categoryViewModel.q)
                    .sortBy("newest")
                    .language(categoryViewModel.language)
                    .pageSize(100)
                    .build()
                getEverything(
                    everythingBuilder,
                    object : ArticlesLiveDataResponseCallback {

                        override fun onFailure(throwable: Throwable) {
                            categoryViewModel.loading.set(false)
                            categoryViewModel.emptyNews.set(true)
                            if (throwable.toString() == ErrorMessage.unknownHostException) {
                                categoryViewModel.emptyText.set("Internet Error")
                            }
                            val props = JSONObject()
                            props.put("source", categoryViewModel.category)
                            props.put("reason", throwable.toString())
                            categoryViewModel.mixpanel.track("onFailure", props)
                        }

                        override fun onSuccess(response: MutableLiveData<ArticleResponse>) {
                            categoryViewModel.loading.set(false)
                            response.observeForever { newsResponse ->
                                categoryViewModel.emptyNews.set(newsResponse.totalResults == 0)
                                if (newsResponse.status == "400") {
                                    categoryViewModel.emptyText.set("Internet Error")
                                }
                                categoryViewModel.newsResponseList.addAll(newsResponse.articles)
                            }
                        }
                    }
                )
            } else {
                val topHeadlinesBuilder = TopHeadlinesBuilder.Builder()
                    .q(categoryViewModel.q)
                    .country(categoryViewModel.country)
                    .category(categoryViewModel.category)
                    .pageSize(100)
                    .build()
                getTopHeadlines(
                    topHeadlinesBuilder,
                    object : ArticlesLiveDataResponseCallback {

                        override fun onFailure(throwable: Throwable) {
                            categoryViewModel.loading.set(false)
                            binding.swipeRefresh.isRefreshing = false
                            categoryViewModel.emptyNews.set(true)
                            if (throwable.toString() == ErrorMessage.unknownHostException) {
                                categoryViewModel.emptyText.set("Internet Error")
                            }
                            val props = JSONObject()
                            props.put("source", categoryViewModel.category)
                            props.put("reason", throwable.toString())
                            categoryViewModel.mixpanel.track("onFailure", props)
                        }

                        override fun onSuccess(response: MutableLiveData<ArticleResponse>) {
                            categoryViewModel.loading.set(false)
                            binding.swipeRefresh.isRefreshing = false

                            response.observeForever { newsResponse ->
                                categoryViewModel.emptyNews.set(newsResponse.totalResults == 0)
                                if (newsResponse.status == "400") {
                                    categoryViewModel.emptyText.set("Internet Error")
                                }
                                categoryViewModel.newsResponseList.addAll(newsResponse.articles)
                            }
                        }
                    }
                )
            }
        }
    }

    fun newInstance(category: String? = "", q: String? = ""): CategoryFragment {
        val args = Bundle()
        args.putString("category", category)
        args.putString("q", q)
        val fragment = CategoryFragment()
        fragment.arguments = args
        return fragment
    }
}
