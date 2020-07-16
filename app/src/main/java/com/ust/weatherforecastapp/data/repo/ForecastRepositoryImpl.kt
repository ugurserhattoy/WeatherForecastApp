package com.ust.weatherforecastapp.data.repo


import androidx.lifecycle.LiveData
//import com.ust.weatherforecastapp.data.db.CurrentLocationDao
import com.ust.weatherforecastapp.data.db.CurrentWeatherDao
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherEntry
import com.ust.weatherforecastapp.data.provider.LocationProvider
import com.ust.weatherforecastapp.data.remote.RemoteWeatherDataSource
import com.ust.weatherforecastapp.data.remote.response.RemoteWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime


class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val remoteWeatherDataSource: RemoteWeatherDataSource,
    private val locationProvider: LocationProvider
//    private val currentLocationDao: CurrentLocationDao
) : ForecastRepository {

    init {
        remoteWeatherDataSource.apply {
            downloadedCurrentWeather.observeForever { newCurrentWeather ->
                persistFetchedCurrentWeather(newCurrentWeather)
            }
        }
    }

    override suspend fun getCurrentWeather(): LiveData<CurrentWeatherEntry> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext currentWeatherDao.getCurrentWeather()
        }
    }
//
//    override suspend fun getCurrentLocation(): LiveData<RemoteWeatherResponse> {
//        return withContext(Dispatchers.IO) {
//            return@withContext currentLocationDao.getLocation()
//        }
//    }


    private fun persistFetchedCurrentWeather(fetchedWeather: RemoteWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.updateCurrentWeather(fetchedWeather.currentWeatherEntry!!)
        }
    }

    private suspend fun initWeatherData() {
        if (isFetchCurrentNeeded(ZonedDateTime.now().minusHours(1)))
            fetchCurrentWeather()
    }

    private suspend fun fetchCurrentWeather() {
        val prefString = locationProvider.getPreferredLocationString()
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