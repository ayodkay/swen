package com.ayodkay.apps.swen.view.home

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.FragmentHomeBinding
import com.ayodkay.apps.swen.helper.adapter.ViewStateAdapter
import com.ayodkay.apps.swen.helper.transitions.PopTransformer
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

private val TAB_TITLES = arrayOf(
    R.string.menu_general,
    R.string.menu_entertainment,
    R.string.menu_sports,
    R.string.menu_business,
    R.string.menu_health,
    R.string.menu_science,
    R.string.menu_technology,
    R.string.menu_corona,
    R.string.menu_beautyy,
    R.string.politics
)

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        FragmentHomeBinding.inflate(inflater, container, false).apply {
            binding = this
        }.root.also {
            it.setOnKeyListener(
                View.OnKeyListener { _, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                        if (viewPager.currentItem == 0) {
                            // If the user is currently looking at the first step, allow the system to handle the
                            // Back button. This calls finish() on this activity and pops the back stack.
                            requireActivity().onBackPressed()
                        } else {
                            // Otherwise, select the previous step.
                            viewPager.currentItem = viewPager.currentItem - 1
                        }
                        parentFragmentManager.popBackStack(
                            null,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )
                        return@OnKeyListener true
                    }
                    false
                }
            )
            it.isFocusableInTouchMode = true
            it.requestFocus()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fm: FragmentManager = parentFragmentManager
        val sa = ViewStateAdapter(fm, lifecycle)

        viewPager = binding.viewPager.apply {
            isUserInputEnabled = false
            isSaveEnabled = false
            adapter = sa
            offscreenPageLimit = 1
            setPageTransformer(PopTransformer())
        }
        val tabLayout: TabLayout = binding.tabs
        for (i in 0..9) {
            tabLayout.addTab(tabLayout.newTab().setText(TAB_TITLES[i]))
        }

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }
}
