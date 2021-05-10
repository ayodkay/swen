package com.ayodkay.apps.swen.view.bookmarks.ui.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.adapter.RoomRecyclerview
import com.ayodkay.apps.swen.helper.room.bookmarks.BookmarkRoomVM
import com.ayodkay.apps.swen.model.News
import java.util.*

class BookmarksFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_bookmarks, container, false)
        val noSaved = root.findViewById<ImageView>(R.id.no_saved)
        val savedRecycle = root.findViewById<RecyclerView>(R.id.saved_recycle)

        val news: ArrayList<News> = arrayListOf()

        val bookmarkModel = ViewModelProvider(this).get(BookmarkRoomVM::class.java)
        var add = true
        bookmarkModel.allBookMarkRoom.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                noSaved.visibility = View.VISIBLE
                savedRecycle.visibility = View.GONE
            } else {
                for (i in it.indices) {
                    if (add) {
                        news.add(
                            News(
                                it[i].source,
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
                add = false
            }

            savedRecycle.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = RoomRecyclerview(news, this@BookmarksFragment, requireContext())
            }
        })

        return root
    }
}