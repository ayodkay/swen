package com.ayodkay.apps.swen.view.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayodkay.apps.swen.databinding.FragmentGeneralBinding
import com.ayodkay.apps.swen.helper.Helper

class TopHeadlinesFragment : Fragment() {
    private var _binding: FragmentGeneralBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGeneralBinding.inflate(inflater, container, false)
        val category = arguments?.getString("category")
        val q = arguments?.getString("q")
        Helper.setupFragment(category, this.requireParentFragment(), binding, q)
        return binding.root
    }

    fun newInstance(category: String, q: String? = ""): TopHeadlinesFragment {
        val args = Bundle()
        args.putString("category", category)
        args.putString("q", q)
        val fragment = TopHeadlinesFragment()
        fragment.arguments = args
        return fragment
    }
}