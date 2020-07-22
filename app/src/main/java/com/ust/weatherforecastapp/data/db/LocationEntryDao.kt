package com.ust.weatherforecastapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ust.weatherforecastapp.data.db.entity.LOCATION_ENTRY_ID
import com.ust.weatherforecastapp.data.db.entity.LocationEntry


@Dao
interface LocationEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateCurrentLocation(locationEntry: LocationEntry)

    @Query("select * from location_entry where id = $LOCATION_ENTRY_ID")
    fun getLocation(): LiveData<LocationEntry>

    @Query("select * from location_entry where id = $LOCATION_ENTRY_ID")
    fun getLocationOffline(): LocationEntry?

    @Query("SELECT name FROM location_entry WHERE id = $LOCATION_ENTRY_ID")
    fun getLocationPlaceName(): String?
}