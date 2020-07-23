package com.ust.weatherforecastapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ust.weatherforecastapp.data.db.entity.*

@Database(
    entities = [CurrentWeatherEntry::class, CurrentWeatherWeather::class, LocationEntry::class],
    version = 1,
    exportSchema = false
)
abstract class ForecastDatabase: RoomDatabase() {
    abstract fun currentWeatherDao(): CurrentWeatherDao
    abstract fun currentWeatherWeatherDao(): CurrentWeatherWeatherDao
    abstract fun locationEntryDao(): LocationEntryDao


    companion object {
        @Volatile private var instance: ForecastDatabase? = null //all of the threads will access to property
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                ForecastDatabase::class.java, "forecast.db")
                .build()
    }
}