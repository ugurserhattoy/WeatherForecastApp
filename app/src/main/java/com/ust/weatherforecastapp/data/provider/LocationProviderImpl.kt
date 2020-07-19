package com.ust.weatherforecastapp.data.provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import com.google.android.gms.location.FusedLocationProviderClient
import com.ust.weatherforecastapp.data.db.entity.WeatherLocation
import com.ust.weatherforecastapp.data.repo.ForecastRepository
import com.ust.weatherforecastapp.forecast.ForecastViewModel
import com.ust.weatherforecastapp.interior.LocationPermissionNotGrantedException
import com.ust.weatherforecastapp.interior.asDeferred
import kotlinx.coroutines.Deferred
import java.util.*

const val USE_DEVICE_LOCATION = "USE_DEVICE_LOCATION"
const val CUSTOM_LOCATION = "CUSTOM_LOCATION"

class LocationProviderImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    context: Context
) : PreferenceProvider(context), LocationProvider {

    private val appContext = context.applicationContext

    lateinit var locationManager: LocationManager
    private var hasGPS = false
    private var hasNetwork = false
    private var locationGPS: Location? = null
    private var locationNetwork: Location? = null

    override suspend fun getPreferredLocationString(): String {
        if (isUsingDeviceLocation()) {
            try {
                val deviceLocation = getLastDeviceLocation().await()
                    ?: return "${getCustomLocationName()}"
                return "${deviceLocation.latitude},${deviceLocation.longitude}"
            }catch (e: LocationPermissionNotGrantedException) {
                return "${getCustomLocationName()}"
            }
        }
        else
            return "${getCustomLocationName()}"
    }

    private fun getCustomLocationName(): String? {
        return preferences.getString(CUSTOM_LOCATION, null)
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastDeviceLocation(): Deferred<Location?> {
        Log.d("LocationProviderImpl", "getLastDeviceLocation")
        if (hasLocationPermission()) {
            Log.d("LocationProviderImpl", "getLastDeviceLocation= hasPermission")
            return fusedLocationProviderClient.lastLocation.asDeferred()
        }
        else
            return (throw LocationPermissionNotGrantedException())
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun isUsingDeviceLocation(): Boolean {
        return preferences.getBoolean(USE_DEVICE_LOCATION, true)
    }

    override suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        val deviceLocationChanged = try {
            hasDeviceLocationChanged(lastWeatherLocation)
        } catch (e: LocationPermissionNotGrantedException) {
            false
        }

        return deviceLocationChanged || hasCustomLocationChanged(lastWeatherLocation)
    }

    private suspend fun hasDeviceLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        if (!isUsingDeviceLocation())
            return false

        val deviceLocation = getLastDeviceLocation().await()
            ?: return false

        // Comparing doubles cannot be done with "=="
        val comparisonThreshold = 0.03
        return Math.abs(deviceLocation.latitude - lastWeatherLocation.lat) > comparisonThreshold &&
                Math.abs(deviceLocation.longitude - lastWeatherLocation.lon) > comparisonThreshold
    }

    private fun hasCustomLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        if (!isUsingDeviceLocation()) {
            val customLocationName = getCustomLocationName()
            return customLocationName != lastWeatherLocation.name
        }
        return false
    }

    /*@SuppressLint("MissingPermission")
    private fun getLastLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGPS || hasNetwork) {
            if (hasGPS){
                Log.d(TAG, "hasGPS")
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0F,
                    object : LocationListener {
                        override fun onLocationChanged(p0: Location) {
                            if (p0!=null) {
                                locationGPS = p0
                                Log.d(TAG, "locationGPS1: $locationGPS")
                                Log.d(TAG, "locationGPS1: " + locationGPS!!.longitude + " | "
                                        + locationGPS!!.latitude)

                            }
                        }
                    })
                val localGPSLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGPSLocation!=null) {
                    locationGPS = localGPSLocation
                    Log.d(TAG, "locationGPS2: $locationGPS")
                }
            }
            if (hasNetwork){
                Log.d(TAG, "hasNetwork")
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F,
                    object : LocationListener {
                        override fun onLocationChanged(p0: Location) {
                            if (p0!=null) {
                                locationNetwork = p0
                                Log.d(TAG, "locationNetwork1: $locationNetwork")
                            }
                        }
                    })
                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation!=null) {
                    locationNetwork = localNetworkLocation
                    Log.d(TAG, "locationNetwork2: $locationNetwork")
                }
            }
            if (locationGPS!=null && locationNetwork!=null){
                if (locationGPS!!.accuracy >= locationNetwork!!.accuracy) {
                    Log.d(TAG, "NetworkLatitude: " + locationNetwork!!.latitude)
                    Log.d(TAG, "NetworkLongitude: " + locationNetwork!!.longitude)
                }else {
                    Log.d(TAG, "GPSLatitude: " + locationGPS!!.latitude)
                    Log.d(TAG, "GPSLongitude: " + locationGPS!!.longitude)
                }
            }
        }else{
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

    }*/

}