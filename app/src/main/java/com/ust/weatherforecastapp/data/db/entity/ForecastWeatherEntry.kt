package com.ust.weatherforecastapp.data.db.entity


import androidx.room.Embedded
import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "future_weather")
data class ForecastWeatherEntry(
//    val clouds: Int,
//    @SerializedName("dew_point")
//    val dewPoint: Double,
    val dt: Int,
    @SerializedName("feels_like")
    @Embedded(prefix = "feels_like_")
    val feelsLike: FeelsLike,
    val humidity: Int,
//    val pressure: Double,
//    val rain: Double,
//    val sunrise: Int,
//    val sunset: Int,
    @Embedded(prefix = "temp_")
    val temp: Temp,
//    val uvi: Double,
    @Embedded(prefix = "weather_")
    val weather: List<WeatherX>,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_speed")
    val windSpeed: Double
)