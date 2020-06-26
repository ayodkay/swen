package com.ayodkay.apps.swen.view.entertainment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ayodkay.apps.swen.helper.NewsApiClient


class EntertainmentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return NewsApiClient.setupFragment("entertainment",this,inflater,container)
    }
}