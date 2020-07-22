package com.ust.weatherforecastapp.data.remote


import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ust.weatherforecastapp.data.db.entity.CurrentWeatherWeather
import com.ust.weatherforecastapp.data.db.entity.LocationEntry
import com.ust.weatherforecastapp.data.db.entity.WeatherLocation
import com.ust.weatherforecastapp.data.remote.response.RemoteWeatherResponse
import com.ust.weatherforecastapp.forecast.contextJ
import com.ust.weatherforecastapp.interior.NoConnectivityException
import com.ust.weatherforecastapp.locationLatLong
import java.lang.reflect.Type
import java.util.*

class RemoteWeatherDataSourceImpl(
    private val remoteWeatherService: RemoteWeatherService,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : RemoteWeatherDataSource {
    private val _downloadedCurrentWeather = MutableLiveData<RemoteWeatherResponse>()
    private val _downloadedCurrentWeatherWeather = MutableLiveData<CurrentWeatherWeather>()
    private val _downloadedLocationEntry = MutableLiveData<LocationEntry>()
//    private val _downloadedCurrentLocation = MutableLiveData<WeatherLocation>()
    override val downloadedCurrentWeather: LiveData<RemoteWeatherResponse>
        get() = _downloadedCurrentWeather
    override val downloadedCurrentWeatherWeather: LiveData<CurrentWeatherWeather>
        get() = _downloadedCurrentWeatherWeather
    override val downloadedLocationEntry: LiveData<LocationEntry>
        get() = _downloadedLocationEntry
//    override val downloadedCurrentLocation: LiveData<WeatherLocation>
//        get() = _downloadedCurrentLocation

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


            val geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(contextJ, Locale.getDefault())

            addresses = geocoder.getFromLocation(
                locationLatLong[0],
                locationLatLong[1],
                1
            )

            Log.d("DataSourceImpl", "addresses= $addresses")
            val preLocationEntry =
                if (addresses[0].adminArea!=null) {
                    "name=${addresses[0].adminArea.toString()}, lat=${locationLatLong[0].toDouble()}, " +
                            "lon=${locationLatLong[1].toDouble()}"
                }
                else if (addresses[0].locality!=null){
                    "name=${addresses[0].locality.toString()}, lat=${locationLatLong[0].toDouble()}, " +
                            "lon=${locationLatLong[1].toDouble()}"
                }else {
                    "name=${addresses[0].subLocality.toString()}, lat=${locationLatLong[0].toDouble()}, " +
                            "lon=${locationLatLong[1].toDouble()}"
                }
            var finalLocationEntry = Gson().toJson(convertStringToMap(preLocationEntry))
            val locationEntry: Type =
                object : TypeToken<LocationEntry?>() {}.type
            val locationEntryPost: LocationEntry? = Gson()
                .fromJson(finalLocationEntry, locationEntry) as LocationEntry?
            Log.d("DataSourceImpl", "locationEntryPost: " + locationEntryPost)
            _downloadedLocationEntry.postValue(locationEntryPost)


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