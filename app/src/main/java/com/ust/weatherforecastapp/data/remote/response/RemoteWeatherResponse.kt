package com.ust.weatherforecastapp.data.remote.response


import com.google.gson.annotations.SerializedName
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherEntry
import com.ust.weatherforecastapp.data.db.entity.ForecastWeatherEntry

data class RemoteWeatherResponse(
    @SerializedName("current")
    val currentWeatherEntry: CurrentWeatherEntry,
    @SerializedName("daily")
    val forecastWeatherEntry: List<ForecastWeatherEntry>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    @SerializedName("timezone_offset")
    val timezoneOffset: Int
)