package com.ust.weatherforecastapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


const val LOCATION_ENTRY_ID = 0

@Entity(tableName = "location_entry")
data class LocationEntry (
    val name: String,
    val lat: Double,
    val lon: Double
){
    @PrimaryKey(autoGenerate = false)
    var id: Int = LOCATION_ENTRY_ID
}