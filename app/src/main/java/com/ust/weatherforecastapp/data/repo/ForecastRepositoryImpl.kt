package com.ust.weatherforecastapp.data.repo


//import com.ust.weatherforecastapp.data.db.CurrentLocationDao
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.ust.weatherforecastapp.data.db.CurrentWeatherDao
import com.ust.weatherforecastapp.data.db.CurrentWeatherWeatherDao
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherEntry
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherWeather
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
    private val remoteWeatherService: RemoteWeatherService
//    private val currentLocationDao: CurrentLocationDao
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

    private fun persistFetchedCurrentWeatherWeather(fetchedWeather: CurrentWeatherWeather) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherWeatherDao.updateCurrentWeatherWeather(fetchedWeather)
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

    private fun updateWeatherListSenselessly() {
        GlobalScope.launch(Dispatchers.IO) {

            val currentWeatherResponse =
                remoteWeatherService.getOneCallForWeather(28.00, 38.00)
            var fetchedData = currentWeatherResponse.toString().substringAfter("[")
                .substringBefore("]")
            fetchedData = fetchedData.substringAfter("Weather")
            fetchedData = fetchedData.removeSurrounding("(",")")
            val fetchedDataMap = convertStringToMap(fetchedData)
            val fetchedDataMapFinal = Gson().toJson(fetchedDataMap.toString())
            Log.d("RepositoryImpl", "fetchedDataMapFinal: " + fetchedDataMapFinal)

//            val weather: Type =
//                object : TypeToken<List<Weather?>?>() {}.type
//            val lcs: List<Weather>? = Gson()
//                .fromJson(fetchedDataMapFinal, weather) as List<Weather>?
//            currentWeatherDao.updateWeather(lcs)

//            currentWeatherDao.updateWeatherId(fetchedDataMap["id"]?.toInt() as Int)
//            currentWeatherDao.updateWeatherMain(fetchedDataMap["main"].toString())
//            currentWeatherDao.updateWeatherDescription(fetchedDataMap["description"].toString())
//            currentWeatherDao.updateWeatherIcon(fetchedDataMap["icon"].toString())


            Log.d("RepositoryImpl", "fetchedData: " + fetchedData)
            Log.d("RepositoryImpl", "fetchedDataMap: " + fetchedDataMap["main"])
        }
    }

    fun convertStringToMap(mapAsString: String): Map<String, String> {
        return mapAsString.split(", ").associate {
            val (left, right) = it.split("=")
            left to right
        }
    }
}