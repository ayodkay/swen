package com.ayodkay.apps.swen.view.bookmarks.ui.links

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.ayodkay.apps.swen.databinding.FragmentLinksBinding
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.room.links.Links
import com.ayodkay.apps.swen.view.WebView

class LinksFragment : Fragment() {
    private val linksViewModel: LinksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FragmentLinksBinding.inflate(inflater, container, false).apply {
        viewModel = linksViewModel
        linksViewModel.nativeAdLoader = MaxNativeAdLoader("08f93b640def0007", context)
        val getLinks = Helper.getLinksDatabase(requireContext()).linksDao().getAll()
        linksViewModel.mutableLink.value = getLinks
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var add = true
        linksViewModel.observableLink.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                linksViewModel.emptyLink.set(true)
            } else {
                for (i in it.indices) {
                    if (add) {
                        linksViewModel.links.add(Links(it[i].id, it[i].link))
                    }
                }
                add = false
            }
        }
        linksViewModel.goToWebView.observe(viewLifecycleOwner) {
            startActivity(Intent(context, WebView::class.java)
                .putExtra("url", it)
                .putExtra("toMain", false))
        }
    }

    override fun onDestroy() {
        // Must destroy native ad or else there will be memory leaks.
        if (linksViewModel.nativeAd != null) {
            // Call destroy on the native ad from any native ad loader.
            linksViewModel.nativeAdLoader.destroy(linksViewModel.nativeAd)
        }

        // Destroy the actual loader itself
        linksViewModel.nativeAdLoader.destroy()

        super.onDestroy()
    }
}