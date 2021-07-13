package com.ayodkay.apps.swen.view.category.beauty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.FragmentGeneralBinding
import com.ayodkay.apps.swen.helper.Helper

class BeautyFragment : Fragment() {
    private var _binding: FragmentGeneralBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneralBinding.inflate(inflater, container, false)
        return Helper.setupFragment(
            frag = this,
            binding = binding,
            q = resources.getString(R.string.menu_beauty),
            isEverything = true
        )
    }
}