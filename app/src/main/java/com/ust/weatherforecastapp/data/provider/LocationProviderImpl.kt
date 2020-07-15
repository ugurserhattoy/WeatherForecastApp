package com.ust.weatherforecastapp.data.provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.ust.weatherforecastapp.interior.LocationPermissionNotGrantedException
import com.ust.weatherforecastapp.interior.asDeferred
import kotlinx.coroutines.Deferred

const val CUSTOM_LOCATION = "CUSTOM_LOCATION"

class LocationProviderImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    context: Context
) : PreferenceProvider(context), LocationProvider {

    private val appContext = context.applicationContext

    override suspend fun getPreferredLocationString(): String {
        try {
            val deviceLocation = getLastDeviceLocation().await()
                ?: return "${getCustomLocationName()}"
            return "${deviceLocation.latitude},${deviceLocation.longitude}"
        } catch (e: LocationPermissionNotGrantedException) {
            return "${getCustomLocationName()}"
        }
    }

    private fun getCustomLocationName(): String? {
        return preferences.getString(CUSTOM_LOCATION, null)
    }

    @SuppressLint("MissingPermission")
    private fun getLastDeviceLocation(): Deferred<Location?> {
        return if (hasLocationPermission())
            fusedLocationProviderClient.lastLocation.asDeferred()
        else
            throw LocationPermissionNotGrantedException()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

}