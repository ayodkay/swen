package com.ayodkay.apps.swen.view.business

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ayodkay.apps.swen.helper.NewsApiClient
import com.ayodkay.apps.swen.viewmodel.NewsViewModel

class BusinessFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return NewsApiClient.setupFragment("business",this,inflater,container)
    }
}