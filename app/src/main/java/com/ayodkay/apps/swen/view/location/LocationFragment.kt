package com.ayodkay.apps.swen.view.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.FragmentLocationBinding
import com.ayodkay.apps.swen.helper.BaseFragment
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.countrypicker.CountryPicker
import com.ayodkay.apps.swen.helper.countrypicker.CountryPickerListener
import com.ayodkay.apps.swen.helper.room.country.Country

class LocationFragment : BaseFragment() {
    private val locationViewModel: LocationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FragmentLocationBinding.inflate(inflater, container, false).apply {
        viewModel = locationViewModel
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed { navigateUp() }
        locationViewModel.loadAd.set(true)
        locationViewModel.showDialogEvent.observe(viewLifecycleOwner) {
            val dialogFragment: DialogFragment? =
                childFragmentManager.findFragmentByTag("CountryPicker") as DialogFragment?
            CountryPicker.getInstance(
                resources.getString(R.string.select_country),
                object : CountryPickerListener {
                    override fun onSelectCountry(
                        name: String,
                        code: String,
                        iso: String,
                        language: String,
                    ) {
                        val db = Helper.getCountryDatabase(requireContext())
                        db.countryDao().delete()
                        db.countryDao().insertAll(Country(iso, language))
                        dialogFragment?.dismiss()
                        navigateTo(LocationFragmentDirections.actionNavLocationToNavMainSwen())
                    }
                }
            ).show(childFragmentManager, "CountryPicker")
        }
    }
}
