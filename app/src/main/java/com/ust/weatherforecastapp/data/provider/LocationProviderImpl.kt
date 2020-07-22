package com.ust.weatherforecastapp.data.provider

import android.Manifest
import android.R.id.message
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.ust.weatherforecastapp.MainActivity
import com.ust.weatherforecastapp.data.db.entity.LocationEntry
import com.ust.weatherforecastapp.data.db.entity.WeatherLocation
import com.ust.weatherforecastapp.forecast.contextJ
import com.ust.weatherforecastapp.interior.LocationPermissionNotGrantedException
import com.ust.weatherforecastapp.interior.asDeferred
import com.ust.weatherforecastapp.locationLatLong
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


const val USE_DEVICE_LOCATION = "USE_DEVICE_LOCATION"
const val CUSTOM_LOCATION = "CUSTOM_LOCATION"

class LocationProviderImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    context: Context
) : PreferenceProvider(context), LocationProvider {

    private val appContext = context.applicationContext
    private val TAG = "LocationProvider"

    override suspend fun getPreferredLocationString(): List<Double> {
        if (isUsingDeviceLocation()) {
            try {
                return listOf(locationLatLong[0], locationLatLong[1])
            }catch (e: LocationPermissionNotGrantedException) {
                return getTheGeolocationOfCustomLocationName(getCustomLocationName())
            }
        }else{
            return getTheGeolocationOfCustomLocationName(getCustomLocationName())
        }
//        if (isUsingDeviceLocation()) {
//            try {
//                val deviceLocation = getLastDeviceLocation().await()
//                    ?: return "${getCustomLocationName()}"
//                return "${deviceLocation.latitude},${deviceLocation.longitude}"
//            }catch (e: LocationPermissionNotGrantedException) {
//                return "${getCustomLocationName()}"
//            }
//        }
//        else
//            return "${getCustomLocationName()}"
    }

    private fun getCustomLocationName(): String? {
        Log.d(TAG, "CustomLocation: ${preferences.getString(CUSTOM_LOCATION, null)}")
        return preferences.getString(CUSTOM_LOCATION, null)
    }

    private fun getTheGeolocationOfCustomLocationName(locationName: String?): List<Double> {
        val addresses: List<Address>
        val geocoder: Geocoder = Geocoder(contextJ, Locale.getDefault())
        addresses = geocoder.getFromLocationName(locationName, 1)
        val addressGeolocation = listOf<Double>(addresses[0].latitude, addresses[0].longitude)
        Log.d(TAG, "addressGeolocation: $addressGeolocation")
        return addressGeolocation
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastDeviceLocation(): List<Double> {
        Log.d("LocationProviderImpl", "getLastDeviceLocation")
        if (hasLocationPermission()) {
            Log.d("LocationProviderImpl", "getLastDeviceLocation= hasPermission")
            return getPreferredLocationString()
        }
        else
            return (throw LocationPermissionNotGrantedException())
    }

//    @SuppressLint("MissingPermission")
//    private suspend fun getLastDeviceLocation(): Deferred<Location?> {
//
//        Log.d("LocationProviderImpl", "getLastDeviceLocation")
//        if (hasLocationPermission()) {
//            Log.d("LocationProviderImpl", "getLastDeviceLocation= hasPermission")
//            return fusedLocationProviderClient.lastLocation.asDeferred()
//        }
//        else
//            return (throw LocationPermissionNotGrantedException())
//    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun isUsingDeviceLocation(): Boolean {
        Log.d(TAG, "DEVICE_LOCATION: ${preferences.getBoolean(USE_DEVICE_LOCATION, true)}")
        return preferences.getBoolean(USE_DEVICE_LOCATION, true)
    }

    override suspend fun hasLocationChanged(lastLocationEntry: LocationEntry): Boolean {
        val deviceLocationChanged = try {
            hasDeviceLocationChanged(lastLocationEntry)
        } catch (e: LocationPermissionNotGrantedException) {
            false
        }

        return deviceLocationChanged || hasCustomLocationChanged(lastLocationEntry)
    }

//    override suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
//        val deviceLocationChanged = try {
//            hasDeviceLocationChanged(lastWeatherLocation)
//        } catch (e: LocationPermissionNotGrantedException) {
//            false
//        }
//
//        return deviceLocationChanged || hasCustomLocationChanged(lastWeatherLocation)
//    }

    private suspend fun hasDeviceLocationChanged(lastLocationEntry: LocationEntry): Boolean {
        if (!isUsingDeviceLocation())
            return false

        val deviceLocation = getLastDeviceLocation()

        // Comparing doubles cannot be done with "=="
        val comparisonThreshold = 0.03
        return Math.abs(deviceLocation[0] - lastLocationEntry.lat) > comparisonThreshold &&
                Math.abs(deviceLocation[1] - lastLocationEntry.lon) > comparisonThreshold
    }

//    private suspend fun hasDeviceLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
//        if (!isUsingDeviceLocation())
//            return false
//
//        val deviceLocation = getLastDeviceLocation().await()
//            ?: return false
//
//        // Comparing doubles cannot be done with "=="
//        val comparisonThreshold = 0.03
//        return Math.abs(deviceLocation[0] - lastWeatherLocation.lat) > comparisonThreshold &&
//                Math.abs(deviceLocation[1] - lastWeatherLocation.lon) > comparisonThreshold
//    }

    private fun hasCustomLocationChanged(lastLocationEntry: LocationEntry): Boolean {
        if (!isUsingDeviceLocation()) {
            val customLocationName = getCustomLocationName()
            return customLocationName != lastLocationEntry.name
        }
        return false
    }

//    private fun hasCustomLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
//        if (!isUsingDeviceLocation()) {
//            val customLocationName = getCustomLocationName()
//            return customLocationName != lastWeatherLocation.name
//        }
//        return false
//    }
}