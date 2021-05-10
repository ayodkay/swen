package com.ayodkay.apps.swen.view.bookmarks.ui.links

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
import com.ayodkay.apps.swen.helper.adapter.LinksAdapter
import com.ayodkay.apps.swen.helper.room.links.Links
import java.util.*

class LinksFragment : Fragment() {

    private lateinit var linksViewModel: LinksViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val links: ArrayList<Links> = arrayListOf()


        linksViewModel = ViewModelProvider(this).get(LinksViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_links, container, false)

        val noSaved = root.findViewById<ImageView>(R.id.no_links_saved)
        val savedRecycle = root.findViewById<RecyclerView>(R.id.saved_links_recycle)

        var add = true
        linksViewModel.links.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                noSaved.visibility = View.VISIBLE
                savedRecycle.visibility = View.GONE
            }else{
                for (i in it.indices) {
                    if (add) {
                        links.add(Links(it[i].id, it[i].link))
                    }
                }
                add = false
            }
            savedRecycle.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = LinksAdapter(requireContext(), links)
            }
        })


        return root
    }
}