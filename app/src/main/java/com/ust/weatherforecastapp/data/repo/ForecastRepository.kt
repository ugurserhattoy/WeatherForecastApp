package com.ust.weatherforecastapp.data.repo

import androidx.lifecycle.LiveData
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherEntry
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherWeather
import com.ust.weatherforecastapp.data.db.entity.WeatherLocation
import com.ust.weatherforecastapp.data.remote.response.RemoteWeatherResponse

interface ForecastRepository {
    suspend fun getCurrentWeather(): LiveData<CurrentWeatherEntry>
    suspend fun getCurrentWeatherWeather(): LiveData<CurrentWeatherWeather>
    suspend fun getCurrentLocation(): LiveData<WeatherLocation>
}