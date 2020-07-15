package com.ust.weatherforecastapp.data.repo

import androidx.lifecycle.LiveData
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherEntry

interface ForecastRepository {
    suspend fun getCurrentWeather(): LiveData<CurrentWeatherEntry>
}