package com.ust.weatherforecastapp.data.repo

import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.LiveData
import com.ust.weatherforecastapp.data.db.CurrentWeatherDao
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherEntry
import com.ust.weatherforecastapp.data.remote.RemoteWeatherDataSource
import com.ust.weatherforecastapp.data.remote.response.RemoteWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val remoteWeatherDataSource: RemoteWeatherDataSource
) : ForecastRepository {

    init {
        remoteWeatherDataSource.downloadedCurrentWeather.observeForever { newCurrentWeather ->
            persistFetchedCurrentWeather(newCurrentWeather)
        }
    }

    override suspend fun getCurrentWeather(): LiveData<CurrentWeatherEntry> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext currentWeatherDao.getCurrentWeather()
        }
    }


    private fun persistFetchedCurrentWeather(fetchedWeather: RemoteWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.updateCurrentWeather(fetchedWeather.currentWeatherEntry)
        }
    }

    private suspend fun initWeatherData() {

    }

    private suspend fun fetchCurrentWeather() {
        val lat = Location.FORMAT_DEGREES.toDouble() //TODO:check what that turns!
        remoteWeatherDataSource.fetchRemoteWeather(
            38.46,
            27.09
        )
    }

    private fun isFetchCurrentNeeded(lastFetchTime: org.threeten.bp.ZonedDateTime): Boolean {
        val thirtyMinutesAgo = org.threeten.bp.ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }
}