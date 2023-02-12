package com.ayodkay.apps.swen.view.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.ayodkay.apps.swen.databinding.FragmentBookmarksBinding
import com.ayodkay.apps.swen.helper.extentions.ifNull
import com.ayodkay.apps.swen.helper.room.bookmarks.BookmarkRoomVM
import com.ayodkay.apps.swen.view.BaseFragment
import com.github.ayodkay.models.Article
import com.github.ayodkay.models.Source

class BookmarksFragment : BaseFragment() {
    private val bookmarksViewModel: BookmarksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentBookmarksBinding.inflate(inflater, container, false).apply {
        viewModel = bookmarksViewModel
        bookmarksViewModel.bookMarkRoom.set(
            ViewModelProvider(requireActivity())[BookmarkRoomVM::class.java]
        )
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookmarksViewModel.bookMarkRoom.get()?.allBookMarkRoom?.observe(viewLifecycleOwner) {
            bookmarksViewModel.news.clear()
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
                        it[i].content
                    )
                )
            }
        }
        bookmarksViewModel.goToViewNewsFragment.observe(viewLifecycleOwner) {
            navigateTo(
                BookmarksFragmentDirections.actionNavigationBookmarksToNavViewNews(
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
}
