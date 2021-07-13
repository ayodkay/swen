package com.ayodkay.apps.swen.view.category.politics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.FragmentGeneralBinding
import com.ayodkay.apps.swen.helper.Helper

class PoliticsFragment : Fragment() {
    private var _binding: FragmentGeneralBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneralBinding.inflate(inflater, container, false)
        return Helper.setupFragment(
            q = getString(R.string.politics), frag = this, binding = binding, isEverything = true
        )
    }
}