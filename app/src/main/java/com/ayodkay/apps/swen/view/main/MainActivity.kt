package com.ayodkay.apps.swen.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
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
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


private const val REQUEST_CODE = 101

private const val JOB_SCHEDULER_ID = 200

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

    private fun onLocationUpdate(location: Location?) {
        location?.run { subscribeCountryName(latitude, longitude) }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    viewModel.locationUpdates.observe(this, this::onLocationUpdate)
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
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE
                )
            }
        } else {
            viewModel.locationUpdates.observe(this, this::onLocationUpdate)
        }
    }

    private fun subscribeCountryName(latitude: Double, longitude: Double) {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        try {
            addresses = geoCoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val addressCode = addresses[0].countryCode.lowercase(Locale.ROOT)
                if (Helper.topCountries(addresses[0].countryCode.lowercase(Locale.ROOT))) {
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
            }
        } catch (ignored: IOException) {
            //do something
        }
    }

    companion object {
        fun scheduleNotification(data: Data, context: Context) {
            val nWorkerParameters =
                PeriodicWorkRequest.Builder(
                    NotifyWork::class.java, 4, TimeUnit.HOURS,
                    30, TimeUnit.MINUTES
                )
                    .setInitialDelay(3, TimeUnit.HOURS).setInputData(data).build()

            WorkManager.getInstance(context).apply {
                enqueueUniquePeriodicWork(
                    NOTIFICATION_WORK, ExistingPeriodicWorkPolicy.REPLACE,
                    nWorkerParameters
                )
            }
        }
    }
}