package com.ayodkay.apps.swen.view.category.sport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayodkay.apps.swen.helper.Helper


class SportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return Helper.setupFragment(
            "sport",
            this,
            inflater,
            container,
            childFragmentManager = childFragmentManager
        )
    }
}