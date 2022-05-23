package com.ayodkay.apps.swen.helper.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.App
import com.ayodkay.apps.swen.view.home.category.CategoryFragment

internal class ViewStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        return getFragment(position)
    }

    override fun getItemCount(): Int {
        // Hardcoded, use lists
        return 10
    }

    private fun getFragment(position: Int): Fragment {
        return when (position) {
            0 -> CategoryFragment().newInstance("general")
            1 -> CategoryFragment().newInstance("entertainment")
            2 -> CategoryFragment().newInstance("sports")
            3 -> CategoryFragment().newInstance("business")
            4 -> CategoryFragment().newInstance("health")
            5 -> CategoryFragment().newInstance("science")
            6 -> CategoryFragment().newInstance("technology")
            7 -> CategoryFragment().newInstance("health", "covid")
            8 -> CategoryFragment().newInstance("", App.context.getString(R.string.menu_beauty))
            else -> CategoryFragment().newInstance("", App.context.getString(R.string.politics))
        }
    }
}
