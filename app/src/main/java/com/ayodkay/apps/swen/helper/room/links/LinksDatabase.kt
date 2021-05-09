package com.ayodkay.apps.swen.helper.room.links

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Links::class], version = 2)
abstract class LinksDatabase : RoomDatabase() {
    abstract fun linksDao(): LinksDao
}