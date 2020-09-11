package com.ayodkay.apps.swen.view.sport

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.NewsApiClient


class SportFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return Helper.setupFragment("sport",this,inflater,container)
    }
}