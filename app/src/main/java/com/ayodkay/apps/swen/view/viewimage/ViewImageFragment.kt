package com.ayodkay.apps.swen.view.viewimage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ayodkay.apps.swen.databinding.FragmentViewImageBinding
import com.ayodkay.apps.swen.view.BaseFragment

class ViewImageFragment : BaseFragment() {
    private val viewImageViewModel: ViewImageViewModel by viewModels()
    private val args: ViewImageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentViewImageBinding.inflate(inflater, container, false).apply {
        viewModel = viewImageViewModel
        viewImageViewModel.image = args.image
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed { navigateUp() }
        viewImageViewModel.loadAd.set(true)
    }
}
