package com.ust.weatherforecastapp.data.remote


import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherWeather
import com.ust.weatherforecastapp.data.db.entity.WeatherLocation
import com.ust.weatherforecastapp.data.remote.response.RemoteWeatherResponse
import com.ust.weatherforecastapp.interior.NoConnectivityException
import java.lang.reflect.Type

class RemoteWeatherDataSourceImpl(
    private val remoteWeatherService: RemoteWeatherService,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : RemoteWeatherDataSource {
    private val _downloadedCurrentWeather = MutableLiveData<RemoteWeatherResponse>()
    private val _downloadedCurrentWeatherWeather = MutableLiveData<CurrentWeatherWeather>()
    private val _downloadedCurrentLocation = MutableLiveData<WeatherLocation>()
    override val downloadedCurrentWeather: LiveData<RemoteWeatherResponse>
        get() = _downloadedCurrentWeather
    override val downloadedCurrentWeatherWeather: LiveData<CurrentWeatherWeather>
        get() = _downloadedCurrentWeatherWeather
    override val downloadedCurrentLocation: LiveData<WeatherLocation>
        get() = _downloadedCurrentLocation

    @SuppressLint("MissingPermission")
    override suspend fun fetchRemoteWeather(latitude: Double, longitude: Double) {
        try {
            val fetchedRemoteWeather = remoteWeatherService
                .getOneCallForWeather(latitude, longitude)
            _downloadedCurrentWeather.postValue(fetchedRemoteWeather)

            var fetchedData = fetchedRemoteWeather.toString().substringAfter("[")
                .substringBefore("]")
            fetchedData = fetchedData.substringAfter("WeatherX")
            fetchedData = fetchedData.removeSurrounding("(",")")
            val fetchedDataMap = convertStringToMap(fetchedData)
            val fetchedDataMapFinal = Gson().toJson(fetchedDataMap)
            val weather: Type =
                object : TypeToken<CurrentWeatherWeather?>() {}.type
            val lcs: CurrentWeatherWeather? = Gson()
                .fromJson(fetchedDataMapFinal, weather) as CurrentWeatherWeather?
            _downloadedCurrentWeatherWeather.postValue(lcs)
            Log.d("DataSourceImpl", "lcs: " + lcs)

//            val postLocation = locationProvider.getPreferredLocationString()

//            val post = fusedLocationProviderClient.lastLocation.toString()
//            Log.d("DataSourceImpl", "post: " + post)
//            val postLocation = Gson().toJson(post)
//            Log.d("DataSourceImpl", "postLocation: " + postLocation)
//            val weatherLocationType: Type =
//                object : TypeToken<WeatherLocation?>() {}.type
//            val adressNLocation: WeatherLocation? = Gson()
//                .fromJson(postLocation, weatherLocationType) as WeatherLocation?
//            _downloadedCurrentLocation.postValue(adressNLocation)
//            Log.d("DataSourceImpl", "adressNLocation: " + adressNLocation)
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