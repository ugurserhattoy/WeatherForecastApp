package com.ust.weatherforecastapp

import android.app.Application
import android.content.Context
import android.location.LocationManager
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import com.google.android.gms.location.LocationServices
import com.jakewharton.threetenabp.AndroidThreeTen
import com.ust.weatherforecastapp.data.db.ForecastDatabase
import com.ust.weatherforecastapp.data.provider.LocationProvider
import com.ust.weatherforecastapp.data.provider.LocationProviderImpl
import com.ust.weatherforecastapp.data.remote.*
import com.ust.weatherforecastapp.data.repo.ForecastRepository
import com.ust.weatherforecastapp.data.repo.ForecastRepositoryImpl
import com.ust.weatherforecastapp.forecast.ForecastFragment
import com.ust.weatherforecastapp.forecast.ForecastViewModelFactory
import org.kodein.di.*
import org.kodein.di.android.x.androidXModule

class ForecastApplication : Application(), DIAware {
    override val di = DI.lazy {
        import(androidXModule(this@ForecastApplication))

        bind() from singleton { ForecastDatabase(instance()) }
        bind() from singleton { instance<ForecastDatabase>().currentWeatherDao() }
        bind() from singleton { instance<ForecastDatabase>().currentWeatherWeatherDao() }
//        bind() from singleton { instance<ForecastDatabase>().currentLocationDao() }
        bind() from singleton { instance<ForecastDatabase>().locationEntryDao() }
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { RemoteWeatherService(instance()) } //DI gets the instance from the above
        bind<RemoteWeatherDataSource>() with singleton { RemoteWeatherDataSourceImpl(instance(), instance()) }
        bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }
        bind() from provider { getSystemService(Context.LOCATION_SERVICE) }
        bind<LocationProvider>() with singleton { LocationProviderImpl(instance(), instance()) }
        bind<ForecastRepository>() with singleton { ForecastRepositoryImpl(instance(), instance(),
            instance(), instance(), instance()) }
        bind() from provider { ForecastViewModelFactory(instance()) }
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)
    }
}