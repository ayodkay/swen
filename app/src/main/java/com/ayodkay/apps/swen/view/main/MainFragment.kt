package com.ayodkay.apps.swen.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayodkay.apps.swen.helper.Helper

class MainFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return Helper.setupFragment(
            "general",
            this,
            inflater,
            container,
            childFragmentManager = childFragmentManager
        )
    }
}