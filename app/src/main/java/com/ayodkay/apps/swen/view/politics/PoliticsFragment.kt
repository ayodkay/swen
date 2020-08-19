package com.ayodkay.apps.swen.view.politics

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.NewsApiClient

class PoliticsFragment:Fragment() {
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return NewsApiClient.setupEveryThingFragment(q= getString(R.string.politics),frag = this,
            inflater = inflater,container = container)
    }
}