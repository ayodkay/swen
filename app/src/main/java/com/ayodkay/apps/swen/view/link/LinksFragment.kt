package com.ayodkay.apps.swen.view.link

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ayodkay.apps.swen.databinding.FragmentLinksBinding
import com.ayodkay.apps.swen.helper.BaseFragment
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.room.links.Links

class LinksFragment : BaseFragment() {
    private val linksViewModel: LinksViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FragmentLinksBinding.inflate(inflater, container, false).apply {
        viewModel = linksViewModel
        val getLinks = Helper.getLinksDatabase(requireContext()).linksDao().getAll()
        linksViewModel.mutableLink.value = getLinks
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linksViewModel.observableLink.observe(viewLifecycleOwner) {
            linksViewModel.links.clear()
            if (it.isEmpty()) {
                linksViewModel.emptyLink.set(true)
            } else {
                for (i in it.indices) {
                    linksViewModel.links.add(Links(it[i].id, it[i].link))
                }
            }
        }
        linksViewModel.goToWebView.observe(viewLifecycleOwner) {
            navigateTo(
                LinksFragmentDirections.actionNavMainLinksToNavWebView(
                    link = it,
                    navigateToMain = false
                )
            )
        }
    }
}
