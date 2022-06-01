package com.ayodkay.apps.swen.helper.room.userlocation

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Location::class], version = 3)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}
