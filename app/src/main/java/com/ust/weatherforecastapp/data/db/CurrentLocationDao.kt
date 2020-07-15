package com.ust.weatherforecastapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ust.weatherforecastapp.data.remote.response.CURRENT_WEATHER_ID
import com.ust.weatherforecastapp.data.remote.response.RemoteWeatherResponse

@Dao
interface CurrentLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateCurrentLocation(locationEntry: RemoteWeatherResponse)

    @Query("select * from current_location where id = $CURRENT_WEATHER_ID")
    fun getLocation(): LiveData<RemoteWeatherResponse>
}