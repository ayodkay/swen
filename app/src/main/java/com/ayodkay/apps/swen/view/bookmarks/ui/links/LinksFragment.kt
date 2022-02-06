package com.ayodkay.apps.swen.view.bookmarks.ui.links

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.FragmentLinksBinding
import com.ayodkay.apps.swen.helper.adapter.LinksAdapter
import com.ayodkay.apps.swen.helper.room.links.Links
import com.mopub.nativeads.MoPubRecyclerAdapter
import com.mopub.nativeads.MoPubStaticNativeAdRenderer
import com.mopub.nativeads.RequestParameters
import com.mopub.nativeads.ViewBinder
import java.util.*

class LinksFragment : Fragment() {

    private lateinit var linksViewModel: LinksViewModel

    private var _binding: FragmentLinksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentLinksBinding.inflate(inflater, container, false)
        linksViewModel = ViewModelProvider(requireActivity())[LinksViewModel::class.java]
        return binding.root.apply {
            val links: ArrayList<Links> = arrayListOf()

            var add = true
            linksViewModel.links.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    binding.noLinksSaved.visibility = View.VISIBLE
                    binding.savedLinksRecycle.visibility = View.GONE
                } else {
                    for (i in it.indices) {
                        if (add) {
                            links.add(Links(it[i].id, it[i].link))
                        }
                    }
                    add = false
                }

                val desiredAssets = EnumSet.of(
                    RequestParameters.NativeAdAsset.TITLE,
                    RequestParameters.NativeAdAsset.TEXT,
                    RequestParameters.NativeAdAsset.ICON_IMAGE,
                    RequestParameters.NativeAdAsset.MAIN_IMAGE,
                    RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT,
                    RequestParameters.NativeAdAsset.SPONSORED
                )
                val requestParameters = RequestParameters.Builder()
                    .desiredAssets(desiredAssets)
                    .build()
                val moPubStaticNativeAdRenderer = MoPubStaticNativeAdRenderer(
                    ViewBinder.Builder(R.layout.native_ad_list_item)
                        .titleId(R.id.native_title)
                        .textId(R.id.native_text)
                        .mainImageId(R.id.native_main_image)
                        .iconImageId(R.id.native_icon_image)
                        .callToActionId(R.id.native_cta)
                        .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                        .sponsoredTextId(R.id.native_sponsored_text_view)
                        .build()
                )

                MoPubRecyclerAdapter(
                    requireActivity(), LinksAdapter(requireContext(), links)
                ).apply {
                    registerAdRenderer(moPubStaticNativeAdRenderer)
                }.also {
                    binding.savedLinksRecycle.apply {
                        adapter = it
                        layoutManager = LinearLayoutManager(requireContext())
                        it.loadAds(getString(R.string.mopub_adunit_native), requestParameters)
                    }
                }
            }
        }
    }
}