package com.ayodkay.apps.swen.view.main

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.FragmentMainBinding
import com.ayodkay.apps.swen.helper.adapter.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


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

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPager: ViewPager2
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root = binding.root
        val sectionsPagerAdapter = SectionsPagerAdapter(requireActivity())
        viewPager = binding.viewPager
        viewPager.isUserInputEnabled = false
        viewPager.adapter = sectionsPagerAdapter
        val tabLayout: TabLayout = binding.tabs
        val tabs = tabLayout.getChildAt(0) as ViewGroup
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getString(TAB_TITLES[position])
        }.attach()
        for (i in 0 until tabs.childCount) {
            val tab = tabs.getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(10, 0, 10, 0)
            tab.requestLayout()
        }
        root.isFocusableInTouchMode = true;
        root.requestFocus()

        root.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                if (viewPager.currentItem == 0) {
                    // If the user is currently looking at the first step, allow the system to handle the
                    // Back button. This calls finish() on this activity and pops the back stack.
                    requireActivity().onBackPressed()
                } else {
                    // Otherwise, select the previous step.
                    viewPager.currentItem = viewPager.currentItem - 1
                }
                parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                return@OnKeyListener true
            }
            false
        })
        return root
    }
}