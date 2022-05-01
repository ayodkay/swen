package com.ayodkay.apps.swen.view.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.FragmentNavControllerBinding

class NavControllerFragment : Fragment() {
    private var _binding: FragmentNavControllerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNavControllerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.inner_host_nav) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bubbleTabBar.addBubbleListener { id ->
            onNavDestinationSelected(id, navController)
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.nav_view_news || destination.id == R.id.nav_view_image) {
                binding.cardview.visibility = View.GONE
            } else {
                binding.cardview.visibility = View.VISIBLE
            }
            binding.bubbleTabBar.setSelectedWithId(destination.id, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun onNavDestinationSelected(
        itemId: Int,
        navController: NavController,
    ): Boolean {
        val builder = NavOptions.Builder()
            .setLaunchSingleTop(true)
        if (navController.currentDestination!!.parent!!.findNode(itemId) is ActivityNavigator.Destination) {
            builder.setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
        } else {
            builder.setEnterAnim(R.animator.nav_default_enter_anim)
                .setExitAnim(R.animator.nav_default_exit_anim)
                .setPopEnterAnim(R.animator.nav_default_pop_enter_anim)
                .setPopExitAnim(R.animator.nav_default_pop_exit_anim)
        }
        //if (itemId == getChildAt(0).id) {
        //builder.setPopUpTo(findStartDestination(navController.graph)!!.id, true)
        // }
        builder.setPopUpTo(itemId, true)
        val options = builder.build()
        return try {
            navController.navigate(itemId, null, options)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}