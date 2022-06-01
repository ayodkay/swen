package com.ayodkay.apps.swen.helper

import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment

open class BaseFragment : Fragment() {

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
