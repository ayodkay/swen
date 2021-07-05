package com.ayodkay.apps.swen.view.business

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayodkay.apps.swen.helper.Helper

class BusinessFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return Helper.setupFragment(
            "business",
            this,
            inflater,
            container,
            childFragmentManager = childFragmentManager
        )
    }
}