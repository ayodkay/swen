package com.ayodkay.apps.swen.view.home.category

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.ayodkay.apps.swen.MainControlDirections
import com.ayodkay.apps.swen.databinding.FragmentGeneralBinding
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.constant.ErrorMessage
import com.ayodkay.apps.swen.helper.room.bookmarks.BookmarkRoomVM
import com.ayodkay.apps.swen.view.AskLocation
import com.github.ayodkay.builder.EverythingBuilder
import com.github.ayodkay.builder.TopHeadlinesBuilder
import com.github.ayodkay.models.ArticleResponse
import com.github.ayodkay.mvvm.interfaces.ArticlesLiveDataResponseCallback

class CategoryFragment : Fragment() {
    private val args: CategoryFragmentArgs by navArgs()
    private val categoryViewModel: CategoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FragmentGeneralBinding.inflate(inflater, container, false).apply {
        viewModel = categoryViewModel
        with(categoryViewModel) {
            category = args.category
            q = args.q
            isEveryThing = args.isEveryThing
            nativeAdLoader = MaxNativeAdLoader("08f93b640def0007", context)
            bookMarkRoom.set(ViewModelProvider(this@CategoryFragment)[BookmarkRoomVM::class.java])
        }
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            categoryViewModel.country =
                Helper.getCountryDatabase(requireContext()).countryDao().getAll().country

            categoryViewModel.language =
                Helper.getCountryDatabase(requireContext()).countryDao().getAll().iso
        } catch (e: Exception) {
            startActivity(Intent(requireContext(), AskLocation::class.java))
            requireActivity().finish()
        }

        with(Helper.setUpNewsClient(requireActivity())) {
            if (categoryViewModel.isEveryThing) {
                val everythingBuilder = EverythingBuilder.Builder()
                    .q(categoryViewModel.q)
                    .sortBy("newest")
                    .language(categoryViewModel.language)
                    .pageSize(100)
                    .build()
                getEverything(everythingBuilder, object : ArticlesLiveDataResponseCallback {

                    override fun onFailure(throwable: Throwable) {
                        categoryViewModel.loading.set(false)
                        categoryViewModel.refreshing.set(false)
                        categoryViewModel.emptyNews.set(true)
                        if (throwable.toString() == ErrorMessage.unknownHostException) {
                            categoryViewModel.emptyText.set("Internet Error")
                        }
                    }

                    override fun onSuccess(response: MutableLiveData<ArticleResponse>) {
                        categoryViewModel.loading.set(false)
                        categoryViewModel.refreshing.set(false)
                        response.observe(viewLifecycleOwner) { newsResponse ->
                            categoryViewModel.emptyNews.set(newsResponse.totalResults == 0)
                            if (newsResponse.status == "400") {
                                categoryViewModel.emptyText.set("Internet Error")
                            }
                            categoryViewModel.newsResponseList.addAll(newsResponse.articles)
                        }
                    }

                })
            } else {
                val topHeadlinesBuilder = TopHeadlinesBuilder.Builder()
                    .q(categoryViewModel.q)
                    .country(categoryViewModel.country)
                    .category(categoryViewModel.category)
                    .pageSize(100)
                    .build()
                getTopHeadlines(topHeadlinesBuilder, object : ArticlesLiveDataResponseCallback {

                    override fun onFailure(throwable: Throwable) {
                        categoryViewModel.loading.set(false)
                        categoryViewModel.refreshing.set(false)
                        categoryViewModel.emptyNews.set(true)
                        if (throwable.toString() == ErrorMessage.unknownHostException) {
                            categoryViewModel.emptyText.set("Internet Error")
                        }
                    }

                    override fun onSuccess(response: MutableLiveData<ArticleResponse>) {
                        categoryViewModel.loading.set(false)
                        categoryViewModel.refreshing.set(false)
                        response.observe(viewLifecycleOwner) { newsResponse ->
                            categoryViewModel.emptyNews.set(newsResponse.totalResults == 0)
                            if (newsResponse.status == "400") {
                                categoryViewModel.emptyText.set("Internet Error")
                            }
                            categoryViewModel.newsResponseList.addAll(newsResponse.articles)
                        }
                    }

                })
            }

        }

        categoryViewModel.goToViewNewsFragment.observe(viewLifecycleOwner) {
            findNavController().navigate(MainControlDirections.actionToViewNews(
                source = it.source.name, url = it.url, image = it.urlToImage, title = it.title,
                content = it.content, description = it.description
            ))
        }
    }

    fun newInstance(
        category: String? = "", q: String? = "", isEverything: Boolean = false,
    ): CategoryFragment {
        val args = Bundle()
        args.putString("category", category)
        args.putString("q", q)
        args.putBoolean("isEverything", isEverything)
        val fragment = CategoryFragment()
        fragment.arguments = args
        return fragment
    }
}