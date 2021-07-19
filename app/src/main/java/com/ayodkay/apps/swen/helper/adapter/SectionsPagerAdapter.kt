package com.ayodkay.apps.swen.helper.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ayodkay.apps.swen.view.category.beauty.BeautyFragment
import com.ayodkay.apps.swen.view.category.business.BusinessFragment
import com.ayodkay.apps.swen.view.category.coronavirus.CoronaFragment
import com.ayodkay.apps.swen.view.category.entertainment.EntertainmentFragment
import com.ayodkay.apps.swen.view.category.general.GeneralFragment
import com.ayodkay.apps.swen.view.category.health.HealthFragment
import com.ayodkay.apps.swen.view.category.politics.PoliticsFragment
import com.ayodkay.apps.swen.view.category.science.ScienceFragment
import com.ayodkay.apps.swen.view.category.sport.SportFragment
import com.ayodkay.apps.swen.view.category.technology.TechnologyFragment

class SectionsPagerAdapter(fa: FragmentActivity) :
    FragmentStateAdapter(fa) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> EntertainmentFragment()
            2 -> SportFragment()
            3 -> BusinessFragment()
            4 -> HealthFragment()
            5 -> ScienceFragment()
            6 -> TechnologyFragment()
            7 -> CoronaFragment()
            8 -> BeautyFragment()
            9 -> PoliticsFragment()
            else -> GeneralFragment()
        }

    }

    override fun getItemCount(): Int {
        return 10
    }

}