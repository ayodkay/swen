package com.ayodkay.apps.swen.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.ActivityMainBinding
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.backend.MyReachability
import com.ayodkay.apps.swen.helper.extentions.ifNull
import com.ayodkay.apps.swen.helper.room.userlocation.Location as LocationDatabase
import com.onesignal.OneSignal
import java.util.*
import org.json.JSONObject

private const val REQUEST_CODE = 101

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val navController get() = findNavController(R.id.nav_host_fragment)

    override fun onStart() {
        super.onStart()
        Helper.goDark(this)
    }

    private val activityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        permissionCheck()
        if (Build.VERSION.SDK_INT >= 33) {
            permissionToPost()
        }
        binding.bubbleTabBar.addBubbleListener { id ->
            onNavDestinationSelected(id, navController)
        }
        val defaultStatusBarColor = window.statusBarColor

        navController.addOnDestinationChangedListener { _, destination, _ ->
            sendAnalytics(destination.id)

            if (activityViewModel.bottomBarIds().contains(destination.id)) {
                binding.cardview.visibility = View.VISIBLE
            } else {
                binding.cardview.visibility = View.GONE
            }

            window.statusBarColor = when (destination.id) {
                R.id.nav_view_news -> Color.TRANSPARENT
                else -> defaultStatusBarColor
            }

            if (destination.id == R.id.nav_view_image) {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
            } else {
                window.clearFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
            }
            binding.bubbleTabBar.setSelectedWithId(destination.id, false)
        }
    }

    private fun onNavDestinationSelected(
        itemId: Int,
        navController: NavController
    ): Boolean {
        val builder = NavOptions.Builder().setLaunchSingleTop(true)
        if (navController.currentDestination!!.parent!!.findNode(itemId) is
            ActivityNavigator.Destination
        ) {
            builder.setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
        } else {
            builder.setEnterAnim(R.animator.nav_default_enter_anim)
                .setExitAnim(R.animator.nav_default_exit_anim)
                .setPopEnterAnim(R.animator.nav_default_pop_enter_anim)
                .setPopExitAnim(R.animator.nav_default_pop_exit_anim)
        }
        builder.setPopUpTo(itemId, true)
        val options = builder.build()
        return try {
            navController.navigate(itemId, null, options)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                with(activityViewModel) {
                    if ((
                        grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED
                        )
                    ) {
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { userLocation: Location? ->
                                userLocation?.let { location ->
                                    if (Build.VERSION.SDK_INT >= 33) {
                                        Geocoder(this@MainActivity, Locale.getDefault())
                                            .getFromLocation(
                                                location.latitude,
                                                location.longitude,
                                                1
                                            ) {
                                                it.firstOrNull()?.let { address ->
                                                    subscribeCountryName(address)
                                                }
                                            }
                                    } else {
                                        Geocoder(this@MainActivity, Locale.getDefault())
                                            .getFromLocation(
                                                location.latitude,
                                                location.longitude,
                                                1
                                            )
                                            ?.firstOrNull()
                                            ?.let { address -> subscribeCountryName(address) }
                                    }
                                }
                            }
                    } else {
                        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                        val countryCodeValue = tm.networkCountryIso
                        subscribeCountryName(
                            Address(Locale.getDefault()).apply {
                                latitude = 40.712742
                                longitude = -74.013382
                                countryCode = countryCodeValue
                            }
                        )
                    }
                }
            }

            REQUEST_POST_PERMISSION -> {
                OneSignal.provideUserConsent(true)
            }
        }
    }

    @RequiresApi(33)
    private fun permissionToPost() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_POST_PERMISSION
            )
        }
    }

    private fun permissionCheck() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }
        } else {
            Thread {
                when {
                    MyReachability.hasInternetConnected(this) -> runOnUiThread {
                        activityViewModel.fusedLocationClient.lastLocation
                            .addOnSuccessListener { userLocation: Location? ->
                                userLocation?.let {
                                    kotlin.runCatching {
                                        if (Build.VERSION.SDK_INT >= 33) {
                                            Geocoder(this, Locale.getDefault())
                                                .getFromLocation(it.latitude, it.longitude, 1) {
                                                    it.firstOrNull()
                                                        ?.let { address ->
                                                            subscribeCountryName(address)
                                                        }
                                                }
                                        } else {
                                            Geocoder(this@MainActivity, Locale.getDefault())
                                                .getFromLocation(it.latitude, it.longitude, 1)
                                                ?.firstOrNull()
                                                ?.let { address -> subscribeCountryName(address) }
                                        }
                                    }
                                }
                            }
                    }
                    else -> runOnUiThread {
                        Toast.makeText(this, getString(R.string.internet_lost), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }.start()
        }
    }

    private fun subscribeCountryName(addresses: Address) {
        val addressCode = addresses.countryCode.lowercase(Locale.ROOT)
        OneSignal.sendTag("country", addressCode)
        with(activityViewModel.getSelectedLocationDao) {
            delete()
            insertAll(
                LocationDatabase(
                    latitude = addresses.latitude,
                    longitude = addresses.longitude,
                    countryCode = addressCode,
                    country = addresses.countryName.ifNull { "default" }
                )
            )
        }
    }

    private fun sendAnalytics(destination: Int) {
        when (destination) {
            R.id.nav_main_swen -> {
                val props = JSONObject().put("source", "Home Fragment")
                activityViewModel.mixpanel.track("Tab Click", props)
            }

            R.id.navigation_bookmarks -> {
                val props = JSONObject().put("source", "Bookmark Fragment")
                activityViewModel.mixpanel.track("Tab Click", props)
            }

            R.id.nav_main_search -> {
                val props = JSONObject().put("source", "Search Fragment")
                activityViewModel.mixpanel.track("Tab Click", props)
            }

            R.id.nav_main_links -> {
                val props = JSONObject().put("source", "Link Fragment")
                activityViewModel.mixpanel.track("Tab Click", props)
            }

            R.id.nav_settings -> {
                val props = JSONObject().put("source", "Settings Fragment")
                activityViewModel.mixpanel.track("Tab Click", props)
            }
        }
    }

    override fun onDestroy() {
        // Must destroy native ad or else there will be memory leaks.
        if (activityViewModel.nativeAd != null) {
            // Call destroy on the native ad from any native ad loader.
            activityViewModel.nativeAdLoader.destroy(activityViewModel.nativeAd)
        }

        // Destroy the actual loader itself
        activityViewModel.nativeAdLoader.destroy()

        super.onDestroy()
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 123
        private const val REQUEST_POST_PERMISSION = 1244
    }
}
