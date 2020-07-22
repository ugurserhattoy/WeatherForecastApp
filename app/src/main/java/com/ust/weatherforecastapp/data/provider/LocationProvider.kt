package com.ust.weatherforecastapp.data.provider

import com.ust.weatherforecastapp.data.db.entity.LocationEntry
import com.ust.weatherforecastapp.data.db.entity.WeatherLocation
import kotlinx.coroutines.Deferred

interface LocationProvider {
    suspend fun getPreferredLocationString(): List<Double>
    suspend fun hasLocationChanged(lastLocationEntry: LocationEntry): Boolean
//    suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean
//    suspend fun getLastLocation(): List<Double>
}