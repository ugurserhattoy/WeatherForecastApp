package com.ust.weatherforecastapp.data.remote

import com.ust.weatherforecastapp.data.remote.response.RemoteWeatherResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "ee5f9e7b202c9fcefaddba717f954e6d"
const val UNITS = "metric"
const val EXCLUDE = "minutely,hourly"


//    https://api.openweathermap.org/data/2.5/onecall?lat=33.441792&lon=-94.037689&units=metric&exclude=minutely,hourly&appid=ee5f9e7b202c9fcefaddba717f954e6d

interface RemoteWeatherService {

    @GET(value = "onecall")
    suspend fun getOneCallForWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): RemoteWeatherResponse

    companion object {
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): RemoteWeatherService {
            val requestInterceptor = Interceptor {chain ->
                val url = chain.request()
                    .url
                    .newBuilder()
                    .addQueryParameter("appid",
                        API_KEY
                    )
                    .addQueryParameter("units",
                        UNITS
                    )
                    .addQueryParameter("exclude",
                        EXCLUDE
                    )
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(connectivityInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RemoteWeatherService::class.java)
        }
    }
}

//This interface is imitated from resocoder.com