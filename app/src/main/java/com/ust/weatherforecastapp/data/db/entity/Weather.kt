package com.ust.weatherforecastapp.data.db.entity


data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)