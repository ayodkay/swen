package com.ayodkay.apps.swen.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.room.Room
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.countrypicker.CountryPicker
import com.ayodkay.apps.swen.helper.countrypicker.CountryPickerListener
import com.ayodkay.apps.swen.helper.room.country.AppDatabase
import com.ayodkay.apps.swen.helper.room.country.Country
import com.ayodkay.apps.swen.view.main.MainActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_ask_location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AskLocation : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_location)

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        select_country.setOnClickListener {
            val picker =
                CountryPicker.getInstance(resources.getString(R.string.select_country), object : CountryPickerListener {
                    override fun onSelectCountry(name: String?, code: String?, iso: String?, language: String?) {
                        GlobalScope.launch {
                            val db = Room.databaseBuilder(
                                applicationContext,
                                AppDatabase::class.java, "country"
                            ).allowMainThreadQueries().build()
                            db.countryDao().delete()
                            db.countryDao().insertAll(
                                Country(
                                    iso!!,
                                    language!!
                                )
                            )
                        }
                        val dialogFragment: DialogFragment? =
                            supportFragmentManager.findFragmentByTag("CountryPicker") as DialogFragment?
                        dialogFragment?.dismiss()

                        startActivity(
                            Intent(
                                this@AskLocation,
                                MainActivity::class.java
                            )
                        )

                        finish()
                    }
                })

            picker.show(supportFragmentManager, "CountryPicker")
        }


    }
}