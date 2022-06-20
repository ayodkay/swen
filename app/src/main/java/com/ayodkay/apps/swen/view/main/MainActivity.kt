package com.ayodkay.apps.swen.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.work.Data
import com.ayodkay.apps.swen.App.Companion.scheduleNotification
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.ActivityMainBinding
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.backend.MyReachability
import com.ayodkay.apps.swen.helper.location.CoGeocoder
import com.ayodkay.apps.swen.helper.location.CoLocation
import com.ayodkay.apps.swen.helper.work.NotifyWork.Companion.NOTIFICATION_ID
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*

private const val REQUEST_CODE = 101

@Suppress("UNCHECKED_CAST")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val navController get() = findNavController(R.id.nav_host_fragment)

    override fun onStart() {
        super.onStart()
        Helper.goDark(this)
    }

    private val activityViewModel: MainActivityViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainActivityViewModel(
                    CoLocation.from(this@MainActivity),
                    CoGeocoder.from(this@MainActivity),
                    this@MainActivity.application
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val data = Data.Builder().putInt(NOTIFICATION_ID, 0).build()
        scheduleNotification(data, this)
        permissionCheck()
        binding.bubbleTabBar.addBubbleListener { id ->
            onNavDestinationSelected(id, navController)
        }
        val defaultStatusBarColor = window.statusBarColor

        navController.addOnDestinationChangedListener { _, destination, _ ->

            if (bottomBarIds().contains(destination.id)) {
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

    private fun bottomBarIds(): ArrayList<Int> {
        return arrayListOf(
            R.id.nav_main_swen, R.id.navigation_bookmarks, R.id.nav_main_search,
            R.id.nav_main_links, R.id.nav_settings
        )
    }

    private fun onNavDestinationSelected(
        itemId: Int,
        navController: NavController,
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

    private fun onAddressUpdate(address: Address?) {
        if (address != null) {
            subscribeCountryName(address)
        }
    }

    private fun onLocationUpdate(location: Location) {}

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_SHOW_SETTINGS -> {
                if ((
                    grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED
                    )
                ) {
                    lifecycle.addObserver(activityViewModel)
                    activityViewModel.addressUpdates.observe(this, this::onAddressUpdate)
                    activityViewModel.locationUpdates.observe(this, this::onLocationUpdate)
                    activityViewModel.resolveSettingsEvent.observe(this) {
                        it.resolve(
                            this,
                            REQUEST_SHOW_SETTINGS
                        )
                    }
                }
            }
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
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_SHOW_SETTINGS
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_SHOW_SETTINGS
                )
            }
        } else {
            Thread {
                when {
                    MyReachability.hasInternetConnected(this) -> runOnUiThread {
                        lifecycle.addObserver(activityViewModel)
                        activityViewModel.addressUpdates.observe(this, this::onAddressUpdate)
                        activityViewModel.locationUpdates.observe(this, this::onLocationUpdate)
                        activityViewModel.resolveSettingsEvent.observe(this) {
                            it.resolve(this, REQUEST_SHOW_SETTINGS)
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
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val isSubscribeToUpdate = sharedPref.getBoolean("isSubscribeToUpdate", false)

        if (!isSubscribeToUpdate) {
            FirebaseMessaging.getInstance()
                .subscribeToTopic("update")
                .addOnCompleteListener {
                    with(sharedPref.edit()) {
                        putBoolean("isSubscribeToUpdate", true)
                        apply()
                    }
                }
        }

        val addressCode = addresses.countryCode.lowercase(Locale.ROOT)
        if (Helper.topCountries(addressCode)) {
            FirebaseMessaging.getInstance()
                .unsubscribeFromTopic("engage")
                .addOnCompleteListener { }

            FirebaseMessaging.getInstance()
                .subscribeToTopic(addressCode)
                .addOnCompleteListener {}
        } else {
            FirebaseMessaging.getInstance()
                .subscribeToTopic("engage")
                .addOnCompleteListener { }
        }
        activityViewModel.stopJob()
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
        private const val REQUEST_SHOW_SETTINGS = 123
    }
}
