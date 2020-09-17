package com.ayodkay.apps.swen.helper.room.userlocation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationDao {
    @Query("SELECT * FROM location")
    fun getAll(): Location

    @Insert
    fun insertAll(vararg location: Location)

    @Query("DELETE FROM location")
    fun delete()
}