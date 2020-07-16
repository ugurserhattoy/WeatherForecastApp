package com.ust.weatherforecastapp.forecast

import androidx.lifecycle.ViewModel
import com.ust.weatherforecastapp.data.repo.ForecastRepository
import com.ust.weatherforecastapp.interior.lazyDeferred

class ForecastViewModel(
    private val forecastRepository: ForecastRepository
) : ViewModel() {
    val weather by lazyDeferred {
        forecastRepository.getCurrentWeather()
    }

//    val weatherLocation by lazyDeferred {
//        forecastRepository.getCurrentLocation()
//    }
}