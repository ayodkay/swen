package com.ayodkay.apps.swen.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.App.Companion.context
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.room.userlocation.Location
import com.ayodkay.apps.swen.notification.jobs.GetTimeJob
import com.ayodkay.apps.swen.view.search.SearchActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import java.io.IOException
import java.util.*

private const val REQUEST_CODE = 101

private const val JOB_SCHEDULER_ID = 200

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onStart() {
        super.onStart()
        Helper.goDark(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startJobScheduler()
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
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
        }

        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null){
                subscribeCountryName(this,it.latitude,it.longitude)
            }
        }


        val toolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        //setUpAlarm(this)ta
        toolbar.setOnMenuItemClickListener { menuItem->
            when (menuItem.itemId) {
                R.id.search -> {
                    startActivity(
                        Intent(
                            this, SearchActivity::class.java
                        )
                    )
                    true
                }

                else -> false
            }
        }

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_main,
                R.id.nav_business,
                R.id.nav_entertainment,
                R.id.nav_health,
                R.id.nav_science,
                R.id.nav_sports,
                R.id.nav_technology,
                R.id.nav_corona,
                R.id.nav_politics,
                R.id.nav_beauty,
                R.id.settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.top_app_bar, menu)

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        subscribeCountryName(this,it.latitude,it.longitude)
                    }
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun subscribeCountryName(context: Context?, latitude: Double, longitude: Double){
        val geoCoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>?
        try {
            addresses = geoCoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val locationDatabase = Helper.getLocationDatabase(this)

                var countryCode = ""

                try {
                    countryCode = locationDatabase.locationDao().getAll().countryCode!!
                }catch (e:Exception){

                }
                if (addresses[0].countryCode.toLowerCase(Locale.ROOT) != countryCode){
                    if (Helper.topCountries(addresses[0].countryCode.toLowerCase(Locale.ROOT))) {
                        locationDatabase.locationDao().delete()
                        locationDatabase.locationDao().insertAll(
                            Location(
                                latitude,
                                longitude,
                                addresses[0].countryCode.toLowerCase(Locale.ROOT),
                                addresses[0].countryName.toLowerCase(Locale.ROOT)
                            )
                        )
                        FirebaseMessaging.getInstance()
                            .unsubscribeFromTopic("engage")
                            .addOnCompleteListener { }

                        FirebaseMessaging.getInstance()
                            .subscribeToTopic(addresses[0].countryCode.toLowerCase(Locale.ROOT))
                            .addOnCompleteListener { }

                    }else{
                        FirebaseMessaging.getInstance()
                            .subscribeToTopic("engage")
                            .addOnCompleteListener { }
                    }
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