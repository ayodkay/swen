package com.ayodkay.apps.swen.helper.room.news

import androidx.lifecycle.LiveData
import com.ayodkay.apps.swen.helper.AppLog

class NewsRoomRepo(private val roomDao: NewsDao){
    val allNewsRoom: LiveData<List<NewsRoom>> = roomDao.getAll()

    suspend fun insert(news: NewsRoom)
    {
        roomDao.insertAll(news)
    }

    fun deleteOne(url: String)
    {
        roomDao.deleteOne(url)
    }


    fun exist(url: String):Boolean
    {
        return roomDao.exist(url)
    }

    suspend fun delete()
    {
        roomDao.delete()
    }
}