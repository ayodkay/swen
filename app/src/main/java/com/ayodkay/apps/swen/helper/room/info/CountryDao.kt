package com.ayodkay.apps.swen.helper.room.info

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CountryDao {
    @Query("SELECT * FROM country")
    fun getAll(): Country

    @Insert
    fun insertAll(vararg country: Country)

    @Query("DELETE FROM country")
    fun delete()
}