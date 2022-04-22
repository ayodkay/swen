package com.ayodkay.apps.swen.view.viewimage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ayodkay.apps.swen.databinding.FragmentViewImageBinding

class ViewImageFragment : Fragment() {
    private val viewImageViewModel: ViewImageViewModel by viewModels()
    private val args: ViewImageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FragmentViewImageBinding.inflate(inflater, container, false).apply {
        viewModel = viewImageViewModel
        val windows = requireActivity().window
        windows.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        viewImageViewModel.imageUrl.set(args.image)
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewImageViewModel.loadAd.set(true)
    }
}