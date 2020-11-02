package com.ayodkay.apps.swen.view.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        context?.startActivity(Intent(context,SettingsActivity::class.java))
        activity?.finish()
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}