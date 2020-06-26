package com.ayodkay.apps.swen.helper.room.info

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Country::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDao
}