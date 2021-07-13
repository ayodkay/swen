package com.ayodkay.apps.swen.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.App.Companion.context
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.location.CoGeocoder
import com.ayodkay.apps.swen.helper.location.CoLocation
import com.ayodkay.apps.swen.notification.jobs.GetTimeJob
import com.google.android.gms.location.*
import com.google.firebase.messaging.FirebaseMessaging
import java.io.IOException
import java.util.*


private const val REQUEST_CODE = 101

private const val JOB_SCHEDULER_ID = 200

class MainActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_main)
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
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun subscribeCountryName(latitude: Double, longitude: Double) {
        val geoCoder = Geocoder(context, Locale.getDefault())
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
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun startJobScheduler() {
            val job = JobInfo.Builder(
                JOB_SCHEDULER_ID, ComponentName(
                    context,
                    GetTimeJob::class.java
                )
            )
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setRequiresCharging(false)
                .build()
            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(job)
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun stopJobScheduler() {
            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.cancel(JOB_SCHEDULER_ID)
        }
    }
}