package com.ust.weatherforecastapp

import android.annotation.SuppressLint
import android.location.LocationManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.ust.weatherforecastapp.data.db.LocationEntryDao
import com.ust.weatherforecastapp.data.db.entity.LocationEntry
import com.ust.weatherforecastapp.data.provider.LocationProvider

class LifecycleBoundLocationManager (
    lifecycleOwner: LifecycleOwner,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
//    private val locationManager: LocationManager,
    private val locationCallback: LocationCallback
//    private val locationProvider: LocationProvider,
//    private val locationEntryDao: LocationEntryDao
    ) : LifecycleObserver {
//        private val lastLocationEntry = locationEntryDao.getLocation().value

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private val locationRequest = LocationRequest().apply {
        interval = 5000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        //TODO:Check how to use requestLocationUpdates
//            locationManager.requestLocationUpdates(locationRequest, locationCallback, 1000,
//                locationProvider.hasLocationChanged(lastLocationEntry as LocationEntry))
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun removeLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

//        private val mLocationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                // do work here
//                locationResult.lastLocation
//                onLocationChanged(locationResult.lastLocation)
//            }
//        }
//
//        fun onLocationChanged(location: Location) {
//            // New location has now been determined
//
//            mLastLocation = location
//            if (mLastLocation != null) {
//    // Update the UI from here
//            }
//  }

}