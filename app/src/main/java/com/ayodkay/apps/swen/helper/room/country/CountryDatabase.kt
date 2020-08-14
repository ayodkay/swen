package com.ayodkay.apps.swen.helper.room.country

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Country::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDao
}