package com.ust.weatherforecastapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val WEATHER_ID = 0

@Entity(tableName = "current_weather_weather")
data class CurrentWeatherWeather(
    val id: Int = 0,
    val main: String = " ",
    val description: String = " ",
    val icon: String = " "
)
{
    @PrimaryKey(autoGenerate = false)
    var wId: Int = WEATHER_ID
}