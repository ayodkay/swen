package com.ayodkay.apps.swen.view.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayodkay.apps.swen.databinding.FragmentGeneralBinding
import com.ayodkay.apps.swen.helper.Helper

class EveryThingFragment : Fragment() {
    private var _binding: FragmentGeneralBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGeneralBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val q = arguments?.getString("q")
        Helper.setupFragment(frag = this, binding = binding, q = q, isEverything = true)
    }

    fun newInstance(q: String): EveryThingFragment {
        val args = Bundle()
        args.putString("q", q)
        val fragment = EveryThingFragment()
        fragment.arguments = args
        return fragment
    }
}