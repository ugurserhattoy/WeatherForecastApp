package com.ust.weatherforecastapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ust.weatherforecastapp.data.db.entity.CURRENT_WEATHER_ID
import com.ust.weatherforecastapp.data.db.entity.WEATHER_LOCATION_ID
import com.ust.weatherforecastapp.data.db.entity.WeatherLocation

@Dao
interface CurrentLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateCurrentLocation(locationEntry: WeatherLocation)

    @Query("select * from weather_location where id = $WEATHER_LOCATION_ID")
    fun getLocation(): LiveData<WeatherLocation>

    @Query("select * from weather_location where id = $WEATHER_LOCATION_ID")
    fun getLocationOffline(): WeatherLocation?
}