package com.ayodkay.apps.swen.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.ayodkay.apps.swen.databinding.ActivityMainBinding
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.location.CoGeocoder
import com.ayodkay.apps.swen.helper.location.CoLocation
import com.ayodkay.apps.swen.helper.work.NotifyWork
import com.ayodkay.apps.swen.helper.work.NotifyWork.Companion.NOTIFICATION_ID
import com.ayodkay.apps.swen.helper.work.NotifyWork.Companion.NOTIFICATION_WORK
import com.google.android.gms.location.*
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*
import java.util.concurrent.TimeUnit

private const val REQUEST_CODE = 101

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onStart() {
        super.onStart()
        Helper.goDark(this)
    }

    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainViewModel(
                    CoLocation.from(this@MainActivity),
                    CoGeocoder.from(this@MainActivity)
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
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    lifecycle.addObserver(viewModel)
                    viewModel.addressUpdates.observe(this, this::onAddressUpdate)
                    viewModel.locationUpdates.observe(this, this::onLocationUpdate)
                    viewModel.resolveSettingsEvent.observe(this) {
                        it.resolve(this,
                            REQUEST_SHOW_SETTINGS)
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
            lifecycle.addObserver(viewModel)
            viewModel.addressUpdates.observe(this, this::onAddressUpdate)
            viewModel.locationUpdates.observe(this, this::onLocationUpdate)
            viewModel.resolveSettingsEvent.observe(this) { it.resolve(this, REQUEST_SHOW_SETTINGS) }
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
        viewModel.stopJob()
    }

    companion object {
        private const val REQUEST_SHOW_SETTINGS = 123
        fun scheduleNotification(data: Data, context: Context) {
            val nWorkerParameters =
                PeriodicWorkRequest.Builder(
                    NotifyWork::class.java, 3, TimeUnit.HOURS,
                    30, TimeUnit.MINUTES
                )
                    .setInitialDelay(2, TimeUnit.HOURS).setInputData(data).build()

            WorkManager.getInstance(context).apply {
                enqueueUniquePeriodicWork(
                    NOTIFICATION_WORK, ExistingPeriodicWorkPolicy.REPLACE,
                    nWorkerParameters
                )
            }
        }
    }
}