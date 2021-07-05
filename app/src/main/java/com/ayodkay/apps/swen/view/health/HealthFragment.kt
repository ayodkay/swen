package com.ayodkay.apps.swen.view.health

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayodkay.apps.swen.helper.Helper

class HealthFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return Helper.setupFragment(
            "health",
            this,
            inflater,
            container,
            childFragmentManager = childFragmentManager
        )
    }
}