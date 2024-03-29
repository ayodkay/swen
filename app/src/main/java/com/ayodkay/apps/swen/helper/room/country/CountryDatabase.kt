package com.ayodkay.apps.swen.helper.room.country

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Country::class], version = 3)
abstract class CountryDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDao
}
