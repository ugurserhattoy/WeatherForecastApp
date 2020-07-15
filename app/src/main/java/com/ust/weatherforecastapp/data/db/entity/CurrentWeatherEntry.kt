package com.ust.weatherforecastapp.data.db.entity


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

const val CURRENT_WEATHER_ID = 0

@Entity(tableName = "current_weather")
data class CurrentWeatherEntry(
//    val clouds: Int,
//    @SerializedName("dew_point")
//    val dewPoint: Double,
    val dt: Int,
    @SerializedName("feels_like")
    val feelsLike: Double,
    val humidity: Int,
//    val pressure: Int,
//    val sunrise: Int,
//    val sunset: Int,
    val temp: Double,
//    val uvi: Double,
//    val visibility: Int,
    @Embedded(prefix = "weather_")
    val weather: List<Weather>,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_speed")
    val windSpeed: Double
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = CURRENT_WEATHER_ID
}