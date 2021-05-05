package com.ayodkay.apps.swen.helper.room.bookmarks

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NewsDao {
    @Query("SELECT * FROM newsroom")
    fun getAll(): LiveData<List<NewsRoom>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(newsRooms: NewsRoom)

    @Query("DELETE FROM newsroom WHERE url LIKE :url")
    fun deleteOne(url: String)

    @Query("SELECT * FROM newsroom WHERE url LIKE :url")
    fun exist(url: String): Boolean

    @Query("DELETE FROM newsroom")
    fun delete()
}