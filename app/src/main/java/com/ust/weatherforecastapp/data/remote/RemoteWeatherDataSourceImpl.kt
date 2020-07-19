package com.ust.weatherforecastapp.data.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherWeather
import com.ust.weatherforecastapp.data.remote.response.RemoteWeatherResponse
import com.ust.weatherforecastapp.interior.NoConnectivityException
import java.lang.reflect.Type

class RemoteWeatherDataSourceImpl(
    private val remoteWeatherService: RemoteWeatherService
) : RemoteWeatherDataSource {
    private val _downloadedCurrentWeather = MutableLiveData<RemoteWeatherResponse>()
    private val _downloadedCurrentWeatherWeather = MutableLiveData<CurrentWeatherWeather>()
    override val downloadedCurrentWeather: LiveData<RemoteWeatherResponse>
        get() = _downloadedCurrentWeather
    override val downloadedCurrentWeatherWeather: LiveData<CurrentWeatherWeather>
        get() = _downloadedCurrentWeatherWeather

    override suspend fun fetchRemoteWeather(latitude: Double, longitude: Double) {
        try {
            val fetchedRemoteWeather = remoteWeatherService
                .getOneCallForWeather(latitude, longitude)
            _downloadedCurrentWeather.postValue(fetchedRemoteWeather)

            Log.d("DataSourceImpl", "fetchedRemoteWeather: " + fetchedRemoteWeather)
            var fetchedData = fetchedRemoteWeather.toString().substringAfter("[")
                .substringBefore("]")
            Log.d("DataSourceImpl", "fetchedData: " + fetchedData)
            fetchedData = fetchedData.substringAfter("WeatherX")
            Log.d("DataSourceImpl", "fetchedData: " + fetchedData)
            fetchedData = fetchedData.removeSurrounding("(",")")
            Log.d("DataSourceImpl", "fetchedData: " + fetchedData)
            val fetchedDataMap = convertStringToMap(fetchedData)
            Log.d("DataSourceImpl", "fetchedDataMap: " + fetchedDataMap)
            val fetchedDataMapFinal = Gson().toJson(fetchedDataMap)
            Log.d("DataSourceImpl", "fetchedDataMapFinal: " + fetchedDataMapFinal)
            val weather: Type =
                object : TypeToken<CurrentWeatherWeather?>() {}.type
            val lcs: CurrentWeatherWeather? = Gson()
                .fromJson(fetchedDataMapFinal, weather) as CurrentWeatherWeather?
            _downloadedCurrentWeatherWeather.postValue(lcs)
            Log.d("DataSourceImpl", "lcs: " + lcs)
        }
        catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
        }
    }

    private fun convertStringToMap(mapAsString: String): Map<String, String> {
        return mapAsString.split(", ").associate {
            val (left, right) = it.split("=")
            left to right
        }
    }
}