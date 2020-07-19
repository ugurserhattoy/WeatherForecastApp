package com.ust.weatherforecastapp.data.repo


import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.ust.weatherforecastapp.data.db.CurrentLocationDao
import com.ust.weatherforecastapp.data.db.CurrentWeatherDao
import com.ust.weatherforecastapp.data.db.CurrentWeatherWeatherDao
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherEntry
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherWeather
import com.ust.weatherforecastapp.data.db.entity.WeatherLocation
import com.ust.weatherforecastapp.data.provider.LocationProvider
import com.ust.weatherforecastapp.data.remote.RemoteWeatherDataSource
import com.ust.weatherforecastapp.data.remote.RemoteWeatherService
import com.ust.weatherforecastapp.data.remote.response.RemoteWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime


class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val currentWeatherWeatherDao: CurrentWeatherWeatherDao,
    private val remoteWeatherDataSource: RemoteWeatherDataSource,
    private val locationProvider: LocationProvider,
    private val remoteWeatherService: RemoteWeatherService,
    private val currentLocationDao: CurrentLocationDao
) : ForecastRepository {

    init {
        remoteWeatherDataSource.apply {
            downloadedCurrentWeather.observeForever { newCurrentWeather ->
                persistFetchedCurrentWeather(newCurrentWeather)
            }
        }
        //TODO: Refactor
        remoteWeatherDataSource.apply {
            downloadedCurrentWeatherWeather.observeForever {
                persistFetchedCurrentWeatherWeather(it)
            }
        }

        remoteWeatherDataSource.apply {
            downloadedCurrentLocation.observeForever {
                persistFetchedCurrentLocation(it)
            }
        }
    }

    override suspend fun getCurrentWeather(): LiveData<CurrentWeatherEntry> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext currentWeatherDao.getCurrentWeather()
        }
    }

    override suspend fun getCurrentWeatherWeather(): LiveData<CurrentWeatherWeather> {
        return withContext(Dispatchers.IO) {
            return@withContext currentWeatherWeatherDao.getCurrentWeatherWeather()
        }
    }



    override suspend fun getCurrentLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO) {
            return@withContext currentLocationDao.getLocation()
        }
    }


    private fun persistFetchedCurrentWeather(fetchedWeather: RemoteWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.updateCurrentWeather(fetchedWeather.currentWeatherEntry!!)
        }
    }

    private fun persistFetchedCurrentWeatherWeather(fetchedWeather: CurrentWeatherWeather) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherWeatherDao.updateCurrentWeatherWeather(fetchedWeather)
        }
    }

    private fun persistFetchedCurrentLocation(fetchedLocation: WeatherLocation) {
        GlobalScope.launch(Dispatchers.IO) {
            currentLocationDao.updateCurrentLocation(fetchedLocation)
        }
    }

    private suspend fun initWeatherData() {
        val lastWeatherLocation = currentLocationDao.getLocation().value

        if (lastWeatherLocation == null
            || locationProvider.hasLocationChanged(lastWeatherLocation)) {
            fetchCurrentWeather()
            return
        }

        if (isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime))
            fetchCurrentWeather()

//        if (isFetchCurrentNeeded(ZonedDateTime.now().minusHours(1)))
//            fetchCurrentWeather()
    }

    private suspend fun fetchCurrentWeather() {
        val prefLocationString = locationProvider.getPreferredLocationString()
        Log.d("RepositoryImpl", "prefLocationString: " + prefLocationString)
        val deviceLatitude = prefLocationString.substringBefore(",")
        Log.d("RepositoryImpl", "deviceLatitude: " + deviceLatitude)
        val deviceLongitude = prefLocationString.substringAfter(", ")
        Log.d("RepositoryImpl", "deviceLongitude: " + deviceLongitude)


        remoteWeatherDataSource.fetchRemoteWeather(
            25.00,
            38.00
        )
    }

    private fun isFetchCurrentNeeded(lastFetchTime: org.threeten.bp.ZonedDateTime): Boolean {
        val thirtyMinutesAgo = org.threeten.bp.ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }

    fun convertStringToMap(mapAsString: String): Map<String, String> {
        return mapAsString.split(", ").associate {
            val (left, right) = it.split("=")
            left to right
        }
    }
}