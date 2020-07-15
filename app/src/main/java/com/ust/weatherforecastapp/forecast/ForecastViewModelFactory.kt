package com.ust.weatherforecastapp.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ust.weatherforecastapp.data.repo.ForecastRepository

class ForecastViewModelFactory(
    private val forecastRepository: ForecastRepository
) : ViewModelProvider.NewInstanceFactory(){

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ForecastViewModel(forecastRepository) as T
    }
}