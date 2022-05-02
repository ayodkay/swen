package com.ayodkay.apps.swen.view.main

import android.annotation.SuppressLint
import android.location.Address
import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.ayodkay.apps.swen.helper.location.CoGeocoder
import com.ayodkay.apps.swen.helper.location.CoLocation
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class MainActivityViewModel(
    private val coLocation: CoLocation, private val coGeocoder: CoGeocoder,
) : ViewModel(), DefaultLifecycleObserver {

    private val locationRequest: LocationRequest = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        //.setSmallestDisplacement(1f)
        //.setNumUpdates(3)
        .setInterval(5000)
        .setFastestInterval(2500)


    private val mutableLocationUpdates: MutableLiveData<Location> = MutableLiveData()
    val locationUpdates: LiveData<Location> = mutableLocationUpdates
    val addressUpdates: LiveData<Address?> = locationUpdates.switchMap { location ->
        liveData { emit(coGeocoder.getAddressFromLocation(location)) }
    }

    private val mutableResolveSettingsEvent: MutableLiveData<CoLocation.SettingsResult.Resolvable> =
        MutableLiveData()
    val resolveSettingsEvent: LiveData<CoLocation.SettingsResult.Resolvable> =
        mutableResolveSettingsEvent

    private var locationUpdatesJob: Job? = null

    override fun onStart(owner: LifecycleOwner) {
        startLocationUpdatesAfterCheck()
    }

    override fun onStop(owner: LifecycleOwner) {
        locationUpdatesJob?.cancel()
        locationUpdatesJob = null
    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdatesAfterCheck() {
        viewModelScope.launch {
            when (val settingsResult = coLocation.checkLocationSettings(locationRequest)) {
                CoLocation.SettingsResult.Satisfied -> {
                    coLocation.getLastLocation()?.run(mutableLocationUpdates::postValue)
                    startLocationUpdates()
                }
                is CoLocation.SettingsResult.Resolvable -> mutableResolveSettingsEvent.postValue(
                    settingsResult)
                else -> { /* Ignore for now, we can't resolve this anyway */
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        locationUpdatesJob?.cancel()
        locationUpdatesJob = viewModelScope.launch {
            try {
                coLocation.getLocationUpdates(locationRequest).collect { location ->
                    Log.d("MainViewModel", "Location update received: $location")
                    mutableLocationUpdates.postValue(location)
                }
            } catch (e: CancellationException) {
                Log.e("MainViewModel", "Location updates cancelled", e)
            }
        }
    }

    fun stopJob() {
        locationUpdatesJob?.cancel()
        locationUpdatesJob = null
    }

}