package com.ayodkay.apps.swen.helper

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.room.Room
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.room.country.CountryDatabase
import com.ayodkay.apps.swen.helper.room.links.LinksDatabase
import com.ayodkay.apps.swen.helper.room.userlocation.LocationDatabase
import com.github.ayodkay.init.NewsApi
import com.github.ayodkay.models.NetworkInterceptorModel
import com.github.ayodkay.models.OfflineCacheInterceptorModel
import com.github.ayodkay.mvvm.client.NewsApiClientWithObserver
import java.util.*

object Helper {

    fun goDark(activity: Activity) {
        when (activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                // Night mode is not active, we're using the light theme
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                activity.setTheme(R.style.AppThemeNight)
            } // Night mode is active, we're using dark theme
        }
    }

    fun getCountryDatabase(context: Context): CountryDatabase {
        return Room.databaseBuilder(
            context,
            CountryDatabase::class.java, "country"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    fun getLinksDatabase(context: Context): LinksDatabase {
        return Room.databaseBuilder(
            context, LinksDatabase::class.java, "links"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    fun getLocationDatabase(context: Context): LocationDatabase {
        return Room.databaseBuilder(
            context,
            LocationDatabase::class.java, "location"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    fun available(country: String): Boolean {
        val ac = arrayListOf(
            "ae", "ar", "at", "au", "be", "bg", "br", "ca", "ch", "cn", "co", "cu", "cz", "de",
            "eg", "fr", "gb", "gr", "hk", "hu", "id", "ie", "il", "in", "it", "jp", "kr", "lt",
            "lv", "ma", "mx", "my", "ng", "nl", "no", "nz", "ph", "pl", "pt", "ro", "rs", "ru",
            "sa", "se", "sg", "si", "sk", "th", "tr", "tw", "ua", "us", "ve", "za"
        )
        if (ac.contains(country.lowercase(Locale.ROOT))) {
            return true
        }
        return false
    }

    fun topCountries(country: String): Boolean {
        val ac = arrayListOf(
            "br", "in", "ar", "us", "ng", "de", "fr", "nl"
        )
        if (ac.contains(country.lowercase(Locale.ROOT))) {
            return true
        }
        return false
    }

    fun setUpNewsClient(activity: ComponentActivity, apiKey: String): NewsApiClientWithObserver {
        NewsApi.init(activity)
        return NewsApiClientWithObserver(
            apiKey, NetworkInterceptorModel(), OfflineCacheInterceptorModel()
        )
    }
}
