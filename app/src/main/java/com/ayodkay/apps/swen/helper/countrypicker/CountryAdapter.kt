package com.ayodkay.apps.swen.helper.countrypicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.countrypicker.CountryAdapter.CountryHolder
import java.text.Normalizer
import java.util.*
import java.util.regex.Pattern

class CountryAdapter(private val mCountryPicker: CountryPicker, listener: CountryPickerListener) :
    RecyclerView.Adapter<CountryHolder>(), Filterable {
    private val mInflater: LayoutInflater = LayoutInflater.from(mCountryPicker.activity)
    private val mListener: CountryPickerListener = listener
    private val mFilteredCountries: MutableList<Country>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryHolder {
        return CountryHolder(mInflater.inflate(R.layout.row, parent, false))
    }

    override fun onBindViewHolder(holder: CountryHolder, position: Int) {
        val country = mFilteredCountries[position]
        holder.textView.text = country.name
        val drawableName =
            "flag_" + country.code!!.toLowerCase(Locale.ENGLISH)
        val drawableId = mCountryPicker.resources
            .getIdentifier(drawableName, "drawable", mCountryPicker.requireActivity().packageName)
        if (drawableId != 0) {
            holder.imageView.setImageDrawable(
                ContextCompat.getDrawable(mCountryPicker.requireActivity(), drawableId)
            )
        }
        holder.itemView.setOnClickListener { mListener.onSelectCountry(country.name, country.code,country.iso,country.language) }
    }

    override fun getItemCount(): Int {
        return mFilteredCountries.size
    }

    inner class CountryHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById<View>(R.id.row_title) as TextView
        var imageView: ImageView = itemView.findViewById<View>(R.id.row_icon) as ImageView

    }

    fun refill(countries: List<Country>?) {
        mFilteredCountries.clear()
        mFilteredCountries.addAll(countries!!)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val results = FilterResults()
                val filteredCountries: MutableList<Country> =
                    ArrayList()
                for (country in CountryPicker.countries) {
                    if (containsIgnoreCaseAndAccents(
                            country.name,
                            constraint as String
                        )
                    ) {
                        filteredCountries.add(country)
                    }
                }
                results.values = filteredCountries
                results.count = filteredCountries.size
                return results
            }

            override fun publishResults(
                constraint: CharSequence,
                results: FilterResults
            ) {
                refill(results.values as List<Country>)
            }
        }
    }

    private fun containsIgnoreCaseAndAccents(
        name: String?,
        constraint: String
    ): Boolean {
        return removeAccents(name)!!.toLowerCase(Locale.getDefault())
            .contains(removeAccents(constraint)!!.toLowerCase(Locale.getDefault()))
    }

    private fun removeAccents(string: String?): String? {
       return ACCENTS_PATTERN.matcher(
            Normalizer.normalize(
                string,
                Normalizer.Form.NFD
            )
        ).replaceAll("")
    }

    companion object {
        private val ACCENTS_PATTERN =
            Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    }

    init {
        mFilteredCountries = ArrayList(CountryPicker.countries)
    }
}