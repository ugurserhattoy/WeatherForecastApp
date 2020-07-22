package com.ust.weatherforecastapp.data.remote

import androidx.lifecycle.LiveData
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherWeather
import com.ust.weatherforecastapp.data.db.entity.LocationEntry
import com.ust.weatherforecastapp.data.db.entity.WeatherLocation
import com.ust.weatherforecastapp.data.remote.response.RemoteWeatherResponse

interface RemoteWeatherDataSource {
    val downloadedCurrentWeather: LiveData<RemoteWeatherResponse>
    val downloadedCurrentWeatherWeather: LiveData<CurrentWeatherWeather>
    val downloadedLocationEntry: LiveData<LocationEntry>
//    val downloadedCurrentLocation: LiveData<WeatherLocation>

    suspend fun fetchRemoteWeather(
        latitude: Double,
        longitude: Double
    )
}