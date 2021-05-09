package com.ayodkay.apps.swen.helper.room.links

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LinksDao {
    @Query("SELECT * FROM links")
    fun getAll(): List<Links>

    @Insert
    fun insertAll(vararg links: Links)

    @Query("SELECT * FROM links WHERE link LIKE :link")
    fun exist(link: String): Boolean

    @Query("DELETE FROM links")
    fun delete()
}