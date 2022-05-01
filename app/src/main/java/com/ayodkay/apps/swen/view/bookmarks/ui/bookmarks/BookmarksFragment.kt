package com.ayodkay.apps.swen.view.bookmarks.ui.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.ayodkay.apps.swen.MainControlDirections
import com.ayodkay.apps.swen.databinding.FragmentBookmarksBinding
import com.ayodkay.apps.swen.helper.room.bookmarks.BookmarkRoomVM
import com.github.ayodkay.models.Article
import com.github.ayodkay.models.Source

class BookmarksFragment : Fragment() {
    private val bookmarksViewModel: BookmarksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FragmentBookmarksBinding.inflate(inflater, container, false).apply {
        viewModel = bookmarksViewModel
        bookmarksViewModel.nativeAdLoader = MaxNativeAdLoader("08f93b640def0007", context)
        bookmarksViewModel.bookMarkRoom.set(ViewModelProvider(requireActivity())[BookmarkRoomVM::class.java])
    }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookmarksViewModel.bookMarkRoom.get()?.allBookMarkRoom?.observe(viewLifecycleOwner) {
            for (i in it.indices) {
                bookmarksViewModel.news.add(
                    Article(
                        Source("", it[i].source, "", "", "", "", ""),
                        it[i].author,
                        it[i].title,
                        it[i].description,
                        it[i].url,
                        it[i].urlToImage,
                        it[i].publishedAt,
                        it[i].content,
                    )
                )
            }
        }
        bookmarksViewModel.goToViewNewsFragment.observe(viewLifecycleOwner) {
            findNavController().navigate(MainControlDirections.actionToViewNews(
                source = it.source.name, url = it.url, image = it.urlToImage, title = it.title,
                content = it.content, description = it.description
            ))
        }
    }

    override fun onDestroy() {
        // Must destroy native ad or else there will be memory leaks.
        if (bookmarksViewModel.nativeAd != null) {
            // Call destroy on the native ad from any native ad loader.
            bookmarksViewModel.nativeAdLoader.destroy(bookmarksViewModel.nativeAd)
        }

        // Destroy the actual loader itself
        bookmarksViewModel.nativeAdLoader.destroy()

        super.onDestroy()
    }
}