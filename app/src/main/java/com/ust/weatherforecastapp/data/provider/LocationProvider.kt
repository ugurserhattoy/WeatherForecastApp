package com.ust.weatherforecastapp.data.provider

import com.ust.weatherforecastapp.data.db.entity.WeatherLocation

interface LocationProvider {
    suspend fun getPreferredLocationString(): String
    suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean
}