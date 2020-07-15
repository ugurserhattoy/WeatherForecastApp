package com.ust.weatherforecastapp.data.remote

import androidx.lifecycle.LiveData
import com.ust.weatherforecastapp.data.remote.response.RemoteWeatherResponse

interface RemoteWeatherDataSource {
    val downloadedCurrentWeather: LiveData<RemoteWeatherResponse>

    suspend fun fetchRemoteWeather(
        latitude: Double,
        longitude: Double
    )
}