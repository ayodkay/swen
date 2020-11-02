package com.ayodkay.apps.swen.view.beauty

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.Helper

class BeautyFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return Helper.setupEveryThingFragment(frag = this,
            inflater = inflater,container = container,q=resources.getString(R.string.menu_beauty))
    }
}