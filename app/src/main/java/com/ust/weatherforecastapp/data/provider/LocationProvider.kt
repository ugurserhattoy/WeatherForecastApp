package com.ust.weatherforecastapp.data.provider

interface LocationProvider {
    suspend fun getPreferredLocationString(): String
}