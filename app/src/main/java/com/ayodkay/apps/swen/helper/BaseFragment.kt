package com.ayodkay.apps.swen.helper

import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment

open class BaseFragment : Fragment() {
    private val baseViewModel: BaseViewModel by viewModels()
    override fun onDestroy() {
        // Must destroy native ad or else there will be memory leaks.
        if (baseViewModel.nativeAd != null) {
            // Call destroy on the native ad from any native ad loader.
            baseViewModel.nativeAdLoader.destroy(baseViewModel.nativeAd)
        }

        // Destroy the actual loader itself
        baseViewModel.nativeAdLoader.destroy()

        super.onDestroy()
    }

    protected fun onBackPressed(action: (() -> Unit)) {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            action.invoke()
        }
    }

    protected fun navigateUp() {
        NavHostFragment.findNavController(this).navigateUp()
    }

    protected fun navigateTo(direction: NavDirections) {
        NavHostFragment.findNavController(this).navigate(direction)
    }

    protected fun navigateTo(destination: Int) {
        NavHostFragment.findNavController(this).navigate(destination)
    }
}
