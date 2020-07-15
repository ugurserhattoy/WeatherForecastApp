package com.ust.weatherforecastapp.data.db.entity


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import com.google.gson.annotations.SerializedName

data class ForecastWeatherEntry(

    val dt: Int,
    @SerializedName("feels_like")
    @Embedded(prefix = "feels_like_")
    val feelsLike: FeelsLike,
    val humidity: Int,
    @Embedded(prefix = "temp_")
    val temp: Temp,
    @Embedded(prefix = "weatherx_")
    val weather: ArrayList<WeatherX>?,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_speed")
    val windSpeed: Double
)
//    val clouds: Int,
//    @SerializedName("dew_point")
//    val dewPoint: Double,
//    val pressure: Double,
//    val rain: Double,
//    val sunrise: Int,
//    val sunset: Int,
//    val uvi: Double,