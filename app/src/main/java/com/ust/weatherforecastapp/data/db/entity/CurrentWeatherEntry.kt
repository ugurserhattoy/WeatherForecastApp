package com.ust.weatherforecastapp.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

const val CURRENT_WEATHER_ID = 0

@Entity(tableName = "current_weather")
data class CurrentWeatherEntry(

    val dt: Int,
    @SerializedName("feels_like")
    val feelsLike: Double,
    val humidity: Double,
    val temp: Double,
    val visibility: Int,
    @Embedded(prefix = "weather_")
    val weather: ArrayList<WeatherX>?,
    @SerializedName("wind_deg")
    val windDeg: Double,
    @SerializedName("wind_speed")
    val windSpeed: Double
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = CURRENT_WEATHER_ID
}

//    val clouds: Int,
//    @SerializedName("dew_point")
//    val dewPoint: Double,
//    val pressure: Int,
//    val sunrise: Int,
//    val sunset: Int,
//    val uvi: Double,