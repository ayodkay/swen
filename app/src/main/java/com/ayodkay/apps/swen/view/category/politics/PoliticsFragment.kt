package com.ayodkay.apps.swen.view.category.politics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.Helper

class PoliticsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return Helper.setupEveryThingFragment(
            q = getString(R.string.politics), frag = this,
            inflater = inflater, container = container, childFragmentManager = childFragmentManager
        )
    }
}