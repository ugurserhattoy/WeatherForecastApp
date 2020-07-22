package com.ust.weatherforecastapp.data.remote.response


import com.google.gson.annotations.SerializedName
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherEntry
import com.ust.weatherforecastapp.data.db.entity.ForecastWeatherEntry
import com.ust.weatherforecastapp.data.db.entity.LocationEntry
import com.ust.weatherforecastapp.data.db.entity.WeatherLocation

//const val CURRENT_WEATHER_ID = 0

//@Entity(tableName = "current_location")
data class RemoteWeatherResponse(

    @SerializedName("current")
    val currentWeatherEntry: CurrentWeatherEntry?,
    @SerializedName("daily")
    val forecastWeatherEntry: List<ForecastWeatherEntry>?,
    val location: WeatherLocation,
    val locationEntry: LocationEntry
//    val lat: Double,
//    val lon: Double,
//    val timezone: String,
//    @SerializedName("timezone_offset")
//    val timezoneOffset: Int

)
//{
//    @PrimaryKey(autoGenerate = false)
//    var id: Int = CURRENT_WEATHER_ID
//
//}