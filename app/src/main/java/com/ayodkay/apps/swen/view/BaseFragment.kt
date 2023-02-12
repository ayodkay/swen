package com.ayodkay.apps.swen.view

import android.os.Bundle
import androidx.activity.addCallback
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.ayodkay.apps.swen.helper.extentions.navigateSafe

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

    protected fun navigateTo(@IdRes resId: Int, args: Bundle? = null) {
        NavHostFragment.findNavController(this).navigateSafe(resId, args)
    }
}
