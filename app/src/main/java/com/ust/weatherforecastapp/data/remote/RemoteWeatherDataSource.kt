package com.ust.weatherforecastapp.data.remote

import androidx.lifecycle.LiveData
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherWeather
import com.ust.weatherforecastapp.data.remote.response.RemoteWeatherResponse

interface RemoteWeatherDataSource {
    val downloadedCurrentWeather: LiveData<RemoteWeatherResponse>
    val downloadedCurrentWeatherWeather: LiveData<CurrentWeatherWeather>

    suspend fun fetchRemoteWeather(
        latitude: Double,
        longitude: Double
    )
}