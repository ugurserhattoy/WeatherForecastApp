package com.ust.weatherforecastapp.data.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ust.weatherforecastapp.data.remote.response.RemoteWeatherResponse
import com.ust.weatherforecastapp.interior.NoConnectivityException

class RemoteWeatherDataSourceImpl(
    private val remoteWeatherService: RemoteWeatherService
) : RemoteWeatherDataSource {
    private val _downloadedCurrentWeather = MutableLiveData<RemoteWeatherResponse>()
    override val downloadedCurrentWeather: LiveData<RemoteWeatherResponse>
        get() = _downloadedCurrentWeather

    override suspend fun fetchRemoteWeather(latitude: Double, longitude: Double) {
        try {
            val fetchedRemoteWeather = remoteWeatherService
                .getOneCallForWeather(latitude, longitude)
            _downloadedCurrentWeather.postValue(fetchedRemoteWeather)
        }
        catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
        }
    }
}