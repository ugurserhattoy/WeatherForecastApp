package com.ust.weatherforecastapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherWeather
import com.ust.weatherforecastapp.data.db.entity.WEATHER_ID


@Dao
interface CurrentWeatherWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateCurrentWeatherWeather(weather: CurrentWeatherWeather)

    @Query("SELECT * FROM current_weather_weather WHERE wId = $WEATHER_ID")
    fun getCurrentWeatherWeather(): LiveData<CurrentWeatherWeather>
}