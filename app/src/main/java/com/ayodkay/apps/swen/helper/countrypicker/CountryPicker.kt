package com.ayodkay.apps.swen.helper.countrypicker

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ayodkay.apps.swen.R
import java.util.*
import java.util.Collections.sort

class CountryPicker : DialogFragment() {
    private var mAdapter: CountryAdapter? = null
    private var mListener: CountryPickerListener? = null
    private var userCountryCodes: List<String>? = null

    /**
     * Create view
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.country_picker, container, false)

        // Set dialog title if show as dialog
        val args = arguments
        if (args != null) {
            val dialogTitle =
                args.getString(DIALOG_TITLE_KEY)
            dialog!!.setTitle(dialogTitle)
            val width = resources.getDimensionPixelSize(R.dimen.cp_dialog_width)
            val height = resources.getDimensionPixelSize(R.dimen.cp_dialog_height)
            dialog!!.window!!.setLayout(width, height)
        }
        val searchEditText =
            view.findViewById<View>(R.id.country_picker_search) as EditText
        val recyclerView =
            view.findViewById<View>(R.id.country_picker_recycler_view) as RecyclerView

        // check if user wants all countries or just specified
        countries = if (userCountryCodes == null) {
            allCountries
        } else {
            getAllCountries(userCountryCodes!!)
        }

        // Sort the countries based on country name
        sort(countries)

        // setup recyclerView
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        mAdapter = CountryAdapter(this, mListener!!)
        recyclerView.adapter = mAdapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                super.onScrollStateChanged(recyclerView, newState)
                hideKeyboard()
            }
        })

        // Search for which countries matched user query
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                mAdapter!!.filter.filter(s)
            }
        })
        return view
    }

    /**
     * Method to hide keyboard if it's open
     */
    fun hideKeyboard() {
        try {
            val activity = activity ?: return
            val input =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            val currentFocus = activity.currentFocus
            if (currentFocus == null || currentFocus.windowToken == null) {
                return
            }
            input.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        } catch (e: Exception) {
            // ignoring any exceptions
        }
    }

    companion object {
        var countries = allCountries
        private const val DIALOG_TITLE_KEY = "dialogTitle"
        fun getInstance(
            dialogTitle: String?,
            listener: CountryPickerListener?
        ): CountryPicker {
            val picker = getInstance(listener)
            val bundle = Bundle()
            bundle.putString(DIALOG_TITLE_KEY, dialogTitle)
            picker.arguments = bundle
            return picker
        }

        private fun getInstance(listener: CountryPickerListener?): CountryPicker {
            val picker = CountryPicker()
            picker.mListener = listener
            return picker
        }

        private val allCountries: List<Country>
            get() {
                val countries: MutableList<Country> = ArrayList()
                for (countryCode in Locale.getISOCountries()) {
                    val country = Country()
                    country.code = countryCode
                    country.name = Locale("", countryCode).displayName
                    country.iso = Locale("", countryCode).isO3Country.substring(0, 2)
                    countries.add(country)
                }
                return countries
            }

        private fun getAllCountries(userCountryCodes: List<String>): List<Country> {
            val countries: MutableList<Country> = ArrayList()
            for (countryCode in Locale.getISOCountries()) {
                if (userCountryCodes.contains(countryCode)) {
                    val country = Country()
                    country.code = countryCode
                    country.name = Locale("", countryCode).displayName
                    country.iso = Locale("", countryCode).isO3Country.substring(0, 2)
                    countries.add(country)
                }
                Log.d("country",countries.toString())
            }
            return countries
        }
    }
}