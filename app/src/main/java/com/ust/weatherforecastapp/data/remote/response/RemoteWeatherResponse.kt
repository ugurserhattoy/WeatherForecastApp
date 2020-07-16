package com.ust.weatherforecastapp.data.remote.response


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherEntry
import com.ust.weatherforecastapp.data.db.entity.ForecastWeatherEntry

//const val CURRENT_WEATHER_ID = 0

//@Entity(tableName = "current_location")
data class RemoteWeatherResponse(

    @SerializedName("current")
    val currentWeatherEntry: CurrentWeatherEntry?,
    @SerializedName("daily")
    val forecastWeatherEntry: List<ForecastWeatherEntry>?,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    @SerializedName("timezone_offset")
    val timezoneOffset: Int
)
//{
//    @PrimaryKey(autoGenerate = false)
//    var id: Int = CURRENT_WEATHER_ID
//
//}